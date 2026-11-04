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
- **Keys belong to the unificator** (additive `unify` API): `Unificator.indexKey(term): IndexKey`. Its contract
  mirrors `hashCode` vs `equals`: if two terms unify, their keys must be equal, or one of them must be a wildcard.
  - `strict` keys numbers by type and value; `naive` normalises integral reals.
  - Today's RETE assumes the `strict`/`naive` behaviour for every unificator. The new design makes that assumption
    explicit and overridable.
  - **Clause tags**: the default keys ignore tags, and collections store and return the exact `Clause` instance, so
    tags survive (extending `TagsPreservationSpec`).
  - A tag-aware unificator that only **restricts** matching (through `handleComparison` rejecting pairs) keeps
    default keys and stays sound.
  - One that **widens** matching (accepting pairs that differ, or replacing them) must override `indexKey` with a
    coarser key, down to `IndexKey.Wildcard`, which disables indexing.
- **`IndexKey`** is a sealed hierarchy: `Exact(value)`, `Wildcard`, and `AnyWithArity(n)`. `AnyWithArity(n)` is
  reserved for future **Patterns** (structures whose functor is a variable). `AnyWithArity` buckets sit next to
  `Wildcard` ones; at the head level, they're a per-arity "pattern procedure" merged with the regular one by global
  order key.
  - A pattern *goal* `F(a)` visits every arity-1 procedure, through a lazily built arity → procedures map, merged by
    order key. That's the only remaining k-way merge, and it only runs for patterns.
  - Supporting Patterns then means extending `indexKey` and the unificator. The stores need no change.
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
- `ListedTheory`/`MutableListedTheory`, and the `listedOf`/`emptyListed` factories (§5). They're no use for
  benchmarking: their pre-rewrite numbers are captured in the baseline JSON, and the small-procedure scan threshold
  `T` is tuned inside the new implementation.
- The current `collections` package, replaced by §5.
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

## 5. Clause collections and API (clean break for `collections`, compatible `Theory`)

### 5.1 Design rules

- **One engine, many shapes.** Every collection is a thin façade over the §3 core:
  - *ordered* shapes use the order-keyed stores;
  - *unordered* shapes use the same stores without order maintenance, so lookups need no merging.
  Indexing, `IndexKey`, unificator pluggability and Pattern readiness come for free everywhere.
- **Kotlin-shaped, not Kotlin-typed.** Names and shapes follow Kotlin collections: `size`, `isEmpty`, `first()`,
  `add`, `addFirst`, `removeFirst`, `asList()`. But clause collections **don't implement
  `kotlin.collections.Collection`**, because two notions of membership coexist:
  - **matching**: unifiable under the collection's `unificator` (what LP code means by "does the KB contain p(X)?");
  - **identity**: *variant* equality, i.e. equal up to variable renaming, ignoring tags.
  `Collection.contains` would force one of them onto `equals`/`hashCode`. Instead, both get explicit names, and
  `asList()`/`asSet()` views provide interop.
- **Read-only vs mutable, as in Kotlin.** `ClauseX` is a read-only view. `MutableClauseX : ClauseX` mutates in
  place.
  - Immutable implementations (from `ClauseX.of(...)`) are persistent: `plus`/`minus` (extension functions, as for
    Kotlin's `List`) return new instances that share untouched procedures with the original.
  - `toMutable()` and `toImmutable()` convert between the two (copying procedure arrays, not re-indexing terms).
- **Collections are not taggable, `Theory` is.** Clause tags are always preserved, because the stored instance is
  returned.
- **Variant equality is a strategy.** `ClauseEquivalence` defaults to structural variant-ignoring-tags, with a
  variant-aware hash. A unificator-derived equivalence can be plugged in at construction for tag-aware users. It
  needs `Term.isVariantOf` and a variant hash in `:core` (additive; also useful for future tabling).

### 5.2 Hierarchy

```
ClauseCollection                       read-only; unificator; size; unification-driven queries (5.3)
├── ClauseMultiSet                     unordered, duplicates allowed; count(clause) by variant
│   └── ClauseSet                      unordered, no two variants
├── ClauseQueue                        ordered (Prolog order); first(); lookups in order
│   └── ClauseDeque                    + last(); ordered at both ends
│       ├── ClauseList                 + positional: get(i), indexOfFirstMatch(q), subList view
│       └── Theory                     + Taggable<Theory>, directives/rules, assertA/assertZ/retract/abolish, …
ClauseMap<V>                           keys by variant (ClauseEquivalence); entriesMatching(q); asMap(): Map<Clause, V>

MutableClauseMultiSet / MutableClauseSet / MutableClauseQueue / MutableClauseDeque / MutableClauseList
MutableClauseMap<V> / MutableTheory   (MutableTheory : Theory, MutableClauseDeque — as today)
```

- **`Theory` is a `ClauseDeque`** with tags and the existing Prolog-flavoured API kept verbatim: `assertA` =
  `addFirst`, `assertZ` = `addLast`, `retract` = remove first match. **No `Theory` signature changes.** Its
  `contains(Clause)`/`contains(Struct)`/`contains(Indicator)` keep their **matching** semantics, which is consistent
  with §5.1.
- `ClauseList` needs positional access. On mutable lists, the first positional access after a retraction compacts
  the store (amortised O(1)). Middle insertion assigns an order key between the neighbours' keys, and relabels the
  keys (an order-maintenance scheme) when there's no gap left. Both details stay internal.
- `ClauseMultiSet`/`ClauseSet` are cheaper than the ordered shapes: no order keys and no ordered bucket maintenance.
  That suits saturation and forward chaining, Datalog-style answer sets, and (later) tabling tables.
- `ClauseMap<V>` attaches data to clauses without touching their tags (provenance, source positions, ProbLog
  probabilities, statistics), with unification-driven retrieval.

### 5.3 Unification-driven queries (on `ClauseCollection`, served by the index)

| Method | Meaning | Status |
|---|---|---|
| `matching(q): Sequence<Clause>` (`Theory.get` delegates) | clauses unifiable with `q`, lazily, in collection order | now |
| `firstMatching(q)`, `anyMatching(q)`, `countMatching(q)` | short-circuiting variants | now |
| `containsVariant(c)`, `countVariants(c)` | identity membership | now |
| `matchingWithMgu(q, fresh = true): Sequence<ClauseMatch>` | `ClauseMatch(clause, mgu)`: one unification per candidate, optionally on a fresh copy of the clause. This is the solver's resolution step (U4) | Step 6 experiment, alongside `candidates` |
| `candidates(q)` | ordered key-filtered superset, with no unification | Step 6 experiment |
| `instancesOf(q)`, `generalisationsOf(q)` | one-way matching (subsumption), with the same index (keys filter both directions) | door open: needs `subsumes` in `:unify` |
| `removeFirstMatching(q)`, `removeAllMatching(q)` (mutable) | `retract`/`retractAll` | now |

`matchingWithMgu` is a better fix for U4 than `candidates`. Determinism detection stays exact, because only
unifying clauses are yielded, and each candidate is renamed and unified once. Non-unifying ones mostly get rejected
by the key pre-check before renaming. Step 6 measures both against today on `nrev`, `determinism` and
`fact-table-*`.

### 5.4 Theory metadata: tags or properties?

Recommendation: **tags as the only storage, typed accessors on top.**

- Tags already flow through every `Theory` operation, and they're excluded from `equals`/`hashCode`, as metadata
  should be. Adding dedicated properties to the `Theory` interface for every new need (source path, encoding,
  module name, load time) grows the API and every implementation.
- Add `TagKey<T>(name)` to `utils.Taggable` (additive): `operator fun <T> get(key: TagKey<T>): T?` plus
  `withTag(key, value)`. Well-known metadata then becomes typed extension properties:
  `val Theory.source: TheorySource? get() = this[TheoryTags.Source]`.
- **Tag propagation rules**, which are undefined today, get documented and tested:
  - unary operations (`assert*`, `retract*`, `abolish`, `setUnificator`, `toMutable`/`toImmutable`) keep the
    receiver's tags;
  - `plus(other)` keeps the left operand's.

### 5.5 Breaking changes (justified)

| Change | Why |
|---|---|
| Remove `ReteTree`, the old `ClauseQueue`/`ClauseMultiSet`/`ClauseCollection` and their mutable variants, and `RetrieveResult` | approved clean break; replaced by §5.2, with no users outside `:theory` |
| Remove `Theory.listedOf`/`MutableTheory.listedOf`/`emptyListed` and the listed implementations | no semantics of their own; not needed for benchmarking (§3) |
| Add `Unificator.indexKey` (abstract members unchanged; default implementation provided) | additive, but custom unificators that widen matching must override it (documented in the changelog) |

Not changed now, but proposed for the next major: `MutableTheory : Theory` means a `Theory` reference can mutate
under its holder. A `Theory` (read-only) / `ImmutableTheory` / `MutableTheory` split, mirroring §5.2, would fix it.
It touches every solver's `ExecutionContext`, so it's out of scope here.

## 6. Steps (one PR each)

| Step | Content | Benchmarks | Risk |
|---|---|---|---|
| **0** | Benchmarks, workloads, `theoryMatchReport`, baseline JSON. Correctness net in `:theory` `commonTest`: `TheoryEquivalencePropertySpec` (random `assertA`/`assertZ`/`get`/`retract`/`retractAll`/`abolish` sequences over a tiny signature with duplicates, directives, nested terms, numerics and **tagged clauses**; `strict`, `naive` and a tag-restricting unificator; every impl vs. a list oracle in `commonTest`; exact sequence equality after every op; pinned seeds; `:test-dsl` `TermArbs`). No main-code change. | baseline | none |
| **1** | Usage quick wins: U1 (builder → `MutableTheory.of`), U3 (one lookup per source); delete `rete/generic/**`. | solver workloads | low |
| **2** | `:core` `isVariantOf` + variant hash; `:unify` `indexKey`/`IndexKey`; `:utils` `TagKey`. Each with specs. | — | low |
| **3** | Core stores (§3) + immutable `Theory` + immutable `ClauseQueue`/`Deque`/`List`/`MultiSet`/`Set`/`Map` façades; switch `Theory.of/indexedOf/empty`. | Lookup, Construction, Iteration, fact-table-*, consult-large, determinism | medium |
| **4** | Mutable stores with generations + `MutableTheory` + mutable façades; drop `buffered()` in `StateRuleSelection`, `Retract.kt`, `Clause.kt` (and `:solve-concurrent`); `LogicalUpdateViewSpec`. | Mutation, Churn, LookupWhileMutating, dynamic-* | medium-high |
| **5** | Delete the RETE, the old `collections`, listed theories, `Cacheable` & co.; document tag propagation; rewrite `clause-db-and-rete.md`; CHANGELOG breaking notes. Then the **checkpoint** full run + README. | full | low |
| **6** | Gated experiments: (a) `matchingWithMgu` vs `candidates` in the solver (§5.3); (b) deep indexing of compound arguments; (c) joint multi-argument indexes or tries; (d) tuning `T` and the selectivity heuristic. | per experiment | — |

Doors kept open: Patterns (`IndexKey.AnyWithArity`, pattern procedures), subsumption queries, tabling (variant
equality/hash, `ClauseSet`/`ClauseMap`), typed theory metadata (`TagKey`).

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
- §5.2 collections are implemented over the shared core; the RETE, old `collections` and listed theories are deleted.
- Clause tags survive every operation; a tag-restricting unificator gives the same results as a full scan
  (property test); tag propagation rules for `Theory` are documented and tested.
