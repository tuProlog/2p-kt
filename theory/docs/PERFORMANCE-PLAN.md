# `:theory` performance refactor: implementation plan (#1019)

Plan for #1019, checked against `develop` at `d3fc692`.

**Guiding principles**

- `Theory` (immutable) serves **static KBs** and is optimised for **reads**.
- `MutableTheory` serves **dynamic KBs** and is optimised for **writes** (plus reads that stay cheap while writes
  interleave).
- Callers across the repo use whichever of the two fits their use.
- Re-engineering the implementation is fine. API breaks are only proposed where well motivated (§5).
- Benchmarks are JVM-only (JMH). The optimisations are algorithmic and allocation-level, not JVM-specific, so they
  should carry over to JS.

## 1. Findings (verified)

### Inside `:theory`

All of #1019's claims hold:

| Issue item | Where |
|---|---|
| Eager listed lookup | `AbstractListedTheory.get`, `DirectiveIndex.get`, `ListedTheory.retract*` |
| Double rebuild on immutable retract | `IndexedTheory.retract*` (`ClauseQueue.of(...)`, then `deepCopy`) |
| Full rebuild on every immutable update | `RootNode.deepCopy`, `AbstractTheory.assertA/Z/plus` |
| `LinkedList` buckets, `indexOfFirst` + `get` | `AtomIndex`, `NumericIndex`, `VariableIndex` |
| Recursive `size` | `RuleNode`, `TopLevelFunctorReteNode`, `FunctorIndexingNode`, `FamilyArityReteNode` |
| No pruning | every `getOrPut` in `rete/custom` |
| O(N·K) merge | `utils.merge` (linear minimum scan) |
| Sequence-based nested-argument traversal | `rete/custom/Utils.firstArguments` |
| Hidden snapshot | `MutableIndexedTheory.clauses` |

Additional findings:

- `collections/rete/generic/**` (11 `internal` files) is dead code; only its own specs use it.
- The public `collections` API (`ClauseQueue`, `ClauseMultiSet`, their mutable variants, `ReteTree`) has no user
  outside `:theory`. It only appears in the docs page `documentation/docs/explanation/clause-db-and-rete.md`.

### How the rest of the repo uses theories

| # | Finding | Where | Cost |
|---|---|---|---|
| U1 | `SolverBuilder.dynamicKb(clauses)` builds `Theory.listedOf(...)`, and `AbstractSolver` turns it into a **`MutableListedTheory`** | `SolverBuilderImpl:127-137`, `AbstractSolver:58` | every call to a dynamic predicate is a full linear scan |
| U2 | Every lookup on a mutable KB is fully materialised (`buffered()` = `toList()`) to get logical-update-view semantics | `StateRuleSelection.selectClauses`, `Retract.kt:48`, `Clause.kt:54` | O(matching clauses), plus a `match` on **all** of them, on every call, even when the first one succeeds deterministically |
| U3 | Each goal looks up every source twice: `ruleSources.any { goal in it }`, then `selectClauses` | `StateRuleSelection:227-228` | double index walk + `match` per source per goal |
| U4 | Clauses are unified twice: the theory filters with `unificator.match`, then the solver `freshCopy()`s and `mgu`s again | `StateRuleSelection` → `StateRuleExecution:20` | up to ~2× head unification per resolution step |
| U5 | `dynamicKb` is already mutable, and `staticKb` only changes in batches (`SideEffect.AddStaticClauses`, `AbstractSolver:115`); `ClausePartitioner` correctly fills `MutableTheory`s | `:solve` | none: the immutable single-clause update path is **not** hot |
| U6 | `toMutableTheory()` on an already mutable theory returns `this` (no copy); `setUnificator` with the same unificator returns `this` | — | none |

U1–U4 sit **outside** `:theory` and are likely worth as much as the internal refactor. U2 and U4 shape the new
design.

## 2. What the LP literature suggests

| Technique | Source | Adopted as |
|---|---|---|
| First-argument indexing, `switch_on_term` (var / constant / list / struct), determinism detection (no choice point when one candidate remains) | Warren, *An Abstract Prolog Instruction Set*, SRI TN 309, 1983; Aït-Kaci, *Warren's Abstract Machine: A Tutorial Reconstruction*, 1991 | per-argument **keys** (§3) |
| **Demand-driven (JIT) multi-argument indexing**: build a hash index on argument *i* the first time a call binds it, only for procedures large enough, and choose the argument with the best expected selectivity at call time | Costa, Sagonas, Lopes, *Demand-Driven Indexing of Prolog Clauses*, ICLP 2007 (YAP); Wielemaker et al., *SWI-Prolog*, TPLP 12(1-2), 2012, and the SWI manual's JIT-indexing section | replaces the first-argument-only RETE (#1019 §8) |
| **Logical update view via generation counters**: every clause records the generation it was born and erased in; a call iterates under the generation current when it started; retracted clauses are tombstoned and reclaimed later | Lindholm, O'Keefe, *Efficient Implementation of a Defensible Semantics for Dynamic Prolog Code*, ICLP 1987; ISO 13211-1 §7.5.4 | `MutableTheory` lookups become lazy **and** safe under mutation: no `buffered()` (U2), no copies |
| Clause pre-check on argument keys before full unification | SWI (`clause` keys), WAM indexing | cheap key comparison before `unificator.match` |
| Tries / star-indexing for large dynamic fact tables | XSB (Ramakrishnan, Swift, Warren et al.) | **not now**: heavier, and pays off only for joint multi-argument access. Reconsidered at the checkpoint if `fact-table-*` numbers ask for joint indexes |
| Persistent (HAMT) clause stores | — | **not adopted**: immutable single updates aren't hot (U5). Predicate-granularity sharing (§3) captures most of the gain at no new dependency |

## 3. Target design

### Shared core (`internal`, `theory/.../theory/impl/core`)

- **Procedure map**: `HashMap<Signature, Procedure>` (functor + arity), plus a directives store. A goal touches
  exactly one procedure. Clause order only matters **within** a procedure for resolution, so cross-procedure merging
  disappears from lookup entirely.
- **Global order** for `clauses`, `rules`, `directives`, `equals`, `hashCode`, `toString`:
  - immutable: the clause array, in order;
  - mutable: a global entry deque with tombstones.
  Either way it's O(N) iteration with no k-way merge. #1019 §7 (heap merge) becomes unnecessary.
- **Argument key** (`switch_on_term` generalised to every argument):
  - atom → its value;
  - integer → its value;
  - real → its value, normalised so that integral reals share the integer's key (the `naive` unificator unifies
    `1` with `1.0`; the `strict` one just over-approximates, and `match` filters);
  - compound → functor/arity;
  - variable → *no key* (wildcard).
- **Soundness assumption** (already implicit in today's RETE, now documented): `match(q, c)` implies, for each
  argument, equal keys or a wildcard on either side. This holds for `strict` and `naive`. A custom `Unificator` that
  violates it can opt out of indexing through a new constructor flag (`indexed = false`), which falls back to scans.
  It's additive, not a break.
- **Procedure lookup**: `get(goal)`
  1. Select the procedure in O(1).
  2. If it has fewer than `T` clauses (`T` ≈ 8, tuned by benchmark), scan it.
  3. Otherwise, among the goal's bound arguments, pick the JIT index with the best expected selectivity. Indexes are
     built on first demand. An argument whose index is useless (for example, all clauses share the key) is marked
     non-indexable and never retried.
  4. Candidates are `bucket(key)` ∪ `wildcards`, a two-pointer merge of two ordered lists.
  5. Per candidate, run the key pre-check on the other bound arguments, then `unificator.match`.
  6. Yield lazily through a hand-written iterator: no `Sequence.filter`/`map` chains, and no allocation per rejected
     candidate.

### `Theory`: immutable, read-optimised (static KB)

- **Layout**: `Procedure` = `Array<Clause>`, plus per-argument indexes published lazily. Each index is a
  `HashMap<Key, IntArray>` of ascending positions plus the `IntArray` of wildcard positions. It's immutable once
  built, so publishing it through a racy `val`-backed slot is safe for `:solve-concurrent` readers.
- **Precomputed**: O(1) `size`/`isEmpty`, cached `hashCode`, and a `clauses` array view.
- **Updates**: `assertA/Z`, `retract*`, `abolish` and `plus` build a new root. It shares every untouched `Procedure`
  together with its already-built JIT indexes, copies the root map (O(P), P = number of procedures) and the one
  changed procedure's array (O(clauses of that predicate)), and `arraycopy`s the global array.
  - The single `retract` copies once (fixes #1019 §2).
  - Batches (`assertZ(Iterable)`, `plus`) copy once per batch.
  - `// shortcut:` a global-array copy is O(N) per single-clause update. That's fine while U5 holds; switch to a
    persistent vector only if a real caller turns up.

### `MutableTheory`: write-optimised (dynamic KB)

- **Entries**: each entry is (clause, order key, born generation, erased generation, argument keys computed once at
  insertion). A global `generation` counter is incremented by every mutation.
- **Procedure**: a growable entry array with head room at both ends, so `assertA` and `assertZ` are amortised O(1).
  JIT argument indexes are **maintained incrementally** on assert: append or prepend to a bucket deque of entry refs.
- **`get`** captures the current generation when called and yields only entries with `born ≤ g < erased`. This is
  the logical update view, lazily, with no copy. In-place appends and prepends write outside the range a live
  iterator has captured, and compaction allocates fresh arrays (copy-on-compact), so live iterators keep working.
  This lets the solver drop `buffered()` (U2) and the old RETE's `ConcurrentModificationException` risk.
- **`retract`**: find the first live match through the index, then set `erased = ++generation`, which is O(1) after
  lookup.
  - `retractAll` does the same for each match.
  - `abolish` unlinks the procedure.
  - A per-procedure tombstone count triggers compaction when tombstones exceed the live entries (amortised O(1)).
    Empty procedures are dropped when compacted, so the topology stays O(live clauses) (#1019 §6).
- `size`/`isEmpty` are O(1) counters (#1019 §5).
- `clauses` is a live, generation-filtered `Iterable` (#1019 §11).
- **Not thread-safe**, as today. It's the same contract `:solve-concurrent` already lives with.

### What goes away

- `rete/custom/**` (the RETE).
- `rete/generic/**` (dead code).
- `Cacheable`/`IndexedClause`/`SituatedIndexedClause` and their cache invalidation (#1019 §§12–13). There are no
  caches left to invalidate.
- `ListedTheory`/`MutableListedTheory` internals (§5).
- `utils.merge` stays untouched: it's no longer on the hot path.

## 4. Benchmarks (before → after)

### Infrastructure

Reuse `examples/src/benchmarks`: JMH via kotlinx-benchmark, `timingBenchmarks` (GC profiler, JSON), `results/`,
`compare.py`. New package `it.unibo.tuprolog.examples.benchmarks.theory`, public API only, so the same sources
compile before and after the rewrite. The old and new implementations can't coexist behind a `@Param`, so the
comparison goes **across commits**:

1. PR 0 adds the benchmarks with no `:theory` change and records `results/<date>-theory-baseline-<commit>.json`.
   That file is the listed theories' only remaining role as a performance baseline.
2. Every PR posts `compare.py baseline.json new.json` for its subset: benchmark, size, before, after, change, B/op.
3. Full runs at the checkpoint (Step 5) and at the end are summarised in `examples/benchmarks/README.md`.

### Micro-benchmarks

`impl` ∈ `immutable | mutable` (the baseline run also covers `listed | mutable-listed`). The workload generator
(`TheoryWorkloads.kt`) is deterministic and seeded, with these shapes:

- `facts`: `p_k(c_i, d_{i mod 50}, i)`
- `rules`
- `nested`: `p_k(f(g(c_i)), i)`
- `zero-arity`
- `mixed`: with directives

| Class | Measures | Params |
|---|---|---|
| `TheoryConstructionBenchmark` | bulk `Theory.of`/`MutableTheory.of`; incremental `assertZ` fold | size 10…100k (bulk), ≤ 1000 (incremental immutable, quadratic today) |
| `TheoryLookupBenchmark` | `get(q).firstOrNull()` (= `contains`), `get(q).count()` | size 10/1000/100k × query `arg0-bound`, `arg1-bound`, `last-bound`, `all-vars`, `zero-arity`, `nested-arg0`, `no-match` |
| `TheoryMutationBenchmark` | one `assertA`/`assertZ`/`retract-first`/`retract-last`/`retractAll`/`abolish` on a pre-built theory (mutable ops paired with their inverse to keep size fixed) | size 10/1000/100k |
| `TheoryChurnBenchmark` | mutable: fresh-key assert + retract, then a lookup, `clauses` iteration and `size` | history 0/10k/100k |
| `TheoryIterationBenchmark` | `clauses`/`rules`/`directives`, `size`, `hashCode`, `equals`, and iterate-after-mutation | size 1000/100k × predicates 1/1000 |
| `LookupWhileMutatingBenchmark` | mutable: iterate `get(p(X))` while asserting into `p/1`, the logical-update-view pattern the solver buffers today | size 1000/100k |

### Solver workloads

New entries in `workloads.pl`, run through the existing `PrologSolverBenchmark`
(`-p timeout=none -p chronometer=direct`). These are the "realistic 2P-Kt workloads" the issue asks for:

- `fact-table-arg0`, `fact-table-arg1`, `fact-table-numeric`: 10k static facts, 10k lookups each.
- `dynamic-table-arg0`: the same, but asserted at runtime. This hits U2.
- `dynamic-counter`: `retract(c(N)), N1 is N+1, assertz(c(N1))`.
- `dynamic-churn`: fresh-key `assertz` + `retract`.
- `consult-large`: load 100k clauses.
- `determinism`: first-argument-deterministic recursion over a 1k-clause predicate. Choice points avoided show up
  as B/op.

### Deterministic counts

`./gradlew :examples:theoryMatchReport`, modelled on `clockReadsReport`: a `CountingUnificator` prints `match` calls
per query and implementation. The numbers are free of noise and directly show index selectivity and laziness.

### Budget

At `-wi 2 -i 3 -w 1s -r 1s -f 1`, a full run takes about 35–40 min on the 4-vCPU container. Per-PR runs are
subsets. The container is noisy: an effect needs to be at least 10% and outside the error bars to count.

## 5. API

No break is required. The proposals below are ordered by how strongly they're motivated.

1. **Document the logical update view on `MutableTheory.get`/`clauses`** (a semantic refinement): today, mutating
   while iterating is undefined (the old RETE can throw `ConcurrentModificationException`). Not breaking.
2. **Deprecate `Theory.listedOf`/`MutableTheory.listedOf`/`emptyListed`**, delegating to the new implementations.
   Behaviour is identical: listed theories have no observable semantics of their own, and small procedures are
   scanned anyway (`T`). Remove them at the next major. The naive list model moves to `commonTest` as the
   **oracle** for property tests.
3. **Deprecate `ReteTree` and `ClauseMultiSet`**; reimplement `ClauseQueue`/`MutableClauseQueue` (public, but
   unused outside `:theory`) over the new core. `ReteTree` names an implementation detail that will no longer
   exist. Removal is breaking, so it waits for the next major. Alternatively, remove them right away if you
   prefer a clean cut.
4. **Optional addition, gated by measurement**: `Theory.candidates(goal: Struct): Sequence<Rule>`, an ordered
   *superset* of `get`, filtered by keys only. The solver unifies anyway (U4), so this saves one unification per
   candidate. The catch: without the `match` filter, `hasNext` sees non-unifying clauses, so choice points stay
   open longer (`ChoicePointContext` relies on `alternatives.hasNext`). Kept only if `determinism` and `nrev` don't
   regress.

## 6. Steps (one PR each)

| Step | Content | Benchmarks | Risk |
|---|---|---|---|
| **0** | Benchmarks, workloads, `theoryMatchReport`, baseline JSON. Correctness net in `:theory` `commonTest`: `TheoryEquivalencePropertySpec` (random `assertA`/`assertZ`/`get`/`retract`/`retractAll`/`abolish` sequences over a tiny signature with duplicates, directives, nested terms and numerics; `strict` and `naive` unificators; every impl vs. the list oracle; exact sequence equality after every op; pinned seeds; `:test-dsl` `TermArbs`). No main-code change. | baseline | none |
| **1** | Usage quick wins, measured on their own: U1 (builder → `MutableTheory.of`); U3 (one lookup per source; fall back to the indicator check only when there are no candidates); delete `rete/generic/**`. | solver workloads | low |
| **2** | Core and immutable `Theory` (§3); switch `Theory.of/indexedOf/empty`; `toMutableTheory` still yields the old mutable impl. | Lookup, Construction, Iteration, fact-table-*, consult-large, determinism | medium |
| **3** | Mutable `MutableTheory` (§3) with generations; then drop `buffered()` in `StateRuleSelection`, `Retract.kt`, `Clause.kt` (and the `:solve-concurrent` counterparts). Add `LogicalUpdateViewSpec` (iterate while asserting and retracting). | Mutation, Churn, LookupWhileMutating, dynamic-* | medium-high |
| **4** | Delete the RETE, `Cacheable` & co. and the listed internals; §5.2 and §5.3 deprecations; rewrite `clause-db-and-rete.md`. | full | low |
| **5** | **Checkpoint**: full run, README section, decide on Step 6 items from the numbers. | full | — |
| **6** | Gated experiments, each kept only if the numbers say so: (a) `candidates` (§5.4); (b) deep indexing of compound arguments (SWI-style, for `nested-arg0`); (c) joint multi-argument indexes or tries for fact tables with no single selective argument; (d) tuning `T` and the selectivity heuristic. | per experiment | — |

Steps 1 and 0 are independent of the redesign and already measurable. Step 1 may well give the largest solver-level
win per line changed.

## 7. Definition of done

- The property test passes on JVM and JS (`:theory:jvmTest`, `:theory:jsNodeTest`) with every implementation equal
  to the oracle, and every existing `:theory` and solver spec still passes.
- `theoryMatchReport`:
  - `firstOrNull` needs ≤ (wildcard prefix + 1) `match` calls;
  - `arg1-bound` needs ≈ bucket-size calls, not procedure-size;
  - a mutable lookup is no longer materialised.
- O(1) `size`; topology bounded by live clauses after churn (asserted in the property test via an `internal`
  counter); immutable single updates leave unaffected procedures untouched.
- Baseline, checkpoint and final results stored, with a README summary on solver workloads.
