# `:theory` performance refactor: implementation plan (#1019)

Concrete plan for #1019, checked against `develop` at `d3fc692`. The issue's analysis holds. This file adds
where each problem sits in the code, what the solver actually exercises, how to benchmark before and after,
and the order of PRs.

## 1. What the code says (verified)

| # | Issue claim | Where | Status |
|---|---|---|---|
| 1 | Eager listed lookup | `AbstractListedTheory.get` (`filter` then `asSequence`), `DirectiveIndex.get` (same), `ListedTheory.retract`/`retractAll` (`clauses.filter { match }` over the whole list) | confirmed |
| 2 | Double rebuild on immutable retract | `IndexedTheory.retract`/`retractAll`: `ClauseQueue.of(unificator, clauses)` (rebuild 1), then `retrieveFirst` on an immutable queue (`deepCopy`, rebuild 2). `retract(Iterable)` builds a `MutableClauseQueue` and wraps it in an immutable `IndexedTheory` | confirmed |
| 3 | Immutable update = full rebuild | `RootNode.deepCopy` = `RootNode(unificator, clauses, isOrdered)`. `AbstractTheory.assertA/assertZ/plus/setUnificator` call `createNewTheory(all clauses + new)` | confirmed |
| 4 | `LinkedList` buckets, double traversal | `utils.dequeOf` is `LinkedList` on JVM. `indexOfFirst` + `removeAt`/`get` in `AtomIndex:84`, `NumericIndex:85`, `VariableIndex:60` | confirmed |
| 5 | Recursive `size` | `RuleNode`, `TopLevelFunctorReteNode`, `FunctorIndexingNode`, `FamilyArityReteNode` sum their children's sizes. `IndexedTheory` hides it with a lazy `sizeCache`; `MutableIndexedTheory` doesn't | confirmed |
| 6 | No pruning | `getOrPut` in `RuleNode`, `TopLevelFunctorReteNode`, `FunctorIndexingNode`, `AtomIndex`, `NumericIndex`, `CompoundIndex`. No removal on empty | confirmed |
| 7 | O(N·K) merge | `utils.merge` (`IterUtils.kt`): linear scan for the minimum over a `pipeline` list, plus `removeAt`. Used by `rete.custom.Utils.merge` | confirmed |
| 9 | Allocating nested-arg traversal | `Utils.firstArguments()` is a `sequence {}` builder, then `drop(n).first()` (`PlatformSpecificUtils`) | confirmed |
| 11 | Hidden snapshot | `MutableIndexedTheory.clauses = queue.toList()` | confirmed |

**Additional findings**

- `collections/rete/generic/**` (11 files, all `internal`) has no production caller, only its own 7 specs. Delete it
  (with its specs) in Step 1, so the refactor doesn't have to carry it.
- **What the solver uses** (`:solve-prolog`): rule selection queries `libraries.asTheory()` (cached immutable indexed),
  `staticKb` (immutable indexed) and `dynamicKb` (**mutable** indexed: `AbstractPrologSolver` always calls
  `toMutableTheory()`). `assert*/retract*` therefore go through the **mutable** path. Immutable single-clause updates
  only happen at load time (`SideEffect.AddStaticClauses`, which applies a batch once). So the solver's hot paths are
  **indexed lookup**, **mutable assert/retract**, and the **ordered merge** behind both. The persistent immutable
  index (issue Step 8) doesn't speed up `:solve-prolog`. It only pays off for API users who build theories
  incrementally. Step 8 is gated accordingly (§4).

## 2. Benchmarks (Step 0, before any change to `:theory`)

### Infrastructure: reuse what exists

`examples/src/benchmarks` already has JMH via kotlinx-benchmark, the `timingBenchmarks` task (GC profiler, JSON
output), `results/` naming (`<date>-<label>-<commit>.json`), and `compare.py` (before/after table). Theory
benchmarks go in a new package `it.unibo.tuprolog.examples.benchmarks.theory` of the same source set. There's no new
module and no new plugin. They target the public API only (`Theory`, `MutableTheory`, `ClauseQueue`, `utils.merge`),
which is what users observe, and they keep compiling across internal rewrites.

Unlike #1020, old and new implementations can't run side by side through a `@Param`, because the old one gets
replaced. So comparisons go **across commits**:

1. PR 0 adds the benchmarks only, then runs them on that commit. The result is
   `results/<date>-theory-baseline-<commit>.json`.
2. Each later PR reruns the subset it affects (`-PjmhArgs='... TheoryLookup'`) and pastes
   `compare.py baseline.json new.json` into the PR description (benchmark, size, platform, before, after, change, B/op).
3. Full reruns happen at the Step 6 checkpoint and at the end. Both are stored in `results/` and summarized in
   `examples/benchmarks/README.md`, the same way #1012/#1020 were.

`compare.py` keys on benchmark + params (it drops only `chronometer`), so it works as is.

### Workload generator: `TheoryWorkloads.kt`

Deterministic and seeded. It builds `size` clauses spread over `predicates` predicates, with the following shapes
(each a `@Param` value):

- `facts`: `p_k(c_i, d_{i mod 50}, i)`. Arg 0 is unique, arg 1 has low cardinality, arg 2 is numeric.
- `rules`: `p_k(c_i, Y) :- q(Y)`.
- `nested`: `p_k(f(g(c_i)), i)`, which exercises nested-first-argument indexing.
- `zero-arity`: `p_k :- true` repeated.
- `mixed`: 90% facts, 10% directives and rules, for iteration, `directives` and `rules`.

### Benchmark classes (JVM, `avgt`, `-prof gc`)

`impl` ∈ `listed | indexed | mutable-listed | mutable-indexed` everywhere unless stated.

| Class | Measures | Params | Configs |
|---|---|---|---:|
| `TheoryConstructionBenchmark` | `Theory.indexedOf(clauses)` etc. (`bulk`); `fold { t, c -> t.assertZ(c) }` (`incremental`) | size 10…100k (bulk), 10/100/1000 (incremental: immutable is quadratic, so 10k+ only multiplies wall time) | 32 |
| `TheoryLookupBenchmark` | `get(q).firstOrNull()`, `get(q).count()` | size 10/1000/100k; query `arg0-bound`, `arg1-bound`, `last-bound`, `all-vars`, `zero-arity`, `nested-arg0`, `no-match` | 168 |
| `TheoryMutationBenchmark` | one op on a pre-built theory: `assertA`, `assertZ`, `retract-first`, `retract-last`, `retractAll` (1 of 10 predicates), `abolish`. Mutable ops are paired with their inverse (assert + retract) so size stays fixed without `Level.Invocation` | size 10/1000/100k | 72 |
| `TheoryChurnBenchmark` | mutable only: assert `p(k_n)` + retract it with a fresh key each time; then `get(p(X)).count()`, `clauses` iteration, `size` | history 0/10k/100k historical keys | 18 |
| `TheoryIterationBenchmark` | `clauses`, `rules`, `directives` iteration, `size`, `hashCode`, `equals` (two separately built equal theories), and "iterate after one mutation" (mutable: forces cache regeneration, i.e. the merge) | size 1000/100k; predicates 1/1000 | 80 |
| `MergeBenchmark` | `utils.merge` of K sorted sequences, N = 100k total, consumed fully and first-only | K 2/16/256/4096 | 8 |

`contains(clause)` is literally `get(clause).any()` (`AbstractTheory`, `ReteTree`), so `firstOrNull` covers it and it
gets no row of its own.

**Solver-level workloads** (the issue requires "realistic 2P-Kt workloads"). Add these to
`benchmarks/workloads.pl` and run them through the existing `PrologSolverBenchmark`
(`-p timeout=none -p chronometer=direct`):

- `fact-table-arg0`: 10k-fact table, 10k lookups bound on arg 0.
- `fact-table-arg1`: same table, lookups bound on arg 1 only (the Step 7 target).
- `dynamic-churn`: `assertz`/`retract` loop with a new key per iteration (Steps 4 and 6).
- `dynamic-counter`: `retract(c(N)), N1 is N+1, assertz(c(N1))`, the most common real-world dynamic pattern.

**Budget.** At `-wi 2 -i 3 -w 1s -r 1s -f 1`, the ~380 configs above take about 35–40 min per full run on the
4-vCPU container, longer if the 100k immutable cases dominate. That's why the per-step reruns are subsets. Changes
inside JMH's error bars count as "no change". The cloud container is noisy: 10% is the minimum effect worth
claiming without repeats.

**Topology/memory** (issue §6): there's no allocation-independent JMH measure, so this is checked by a test instead
(§3, `TheoryChurnSpec`). `-prof gc` B/op covers allocations.

**JS**: `:examples` is JVM-only. JS benchmarking would need a new KMP benchmark module with kotlinx-benchmark's JS
target. Default: JVM only. JS stays covered by the test suite (`jsNodeTest`) for correctness (see open question).

## 3. Correctness safety net (Step 0, lands with the benchmarks)

All of these go in `:theory` `commonTest`, follow the Kotest conventions in `AGENTS.md`, and run on JVM and JS.

- **`TheoryEquivalencePropertySpec`** (`kotest-property`, pinned `PropTestConfig(seed, iterations)`): random
  operation sequences (`assertA`, `assertZ`, `get`, `retract`, `retractAll`, `abolish`, `retract(Iterable)`) over a
  tiny signature (`p/0`, `p/1`, `p/2`, `q/2` with nested `f/1`, duplicates, directives), applied to all four impls
  and to `ClauseQueue`/`MutableClauseQueue`. After every op, it compares the `clauses` sequence, the `get` result
  sequence and the retracted clauses (exact sequence equality, never relaxed). Terms come from `:test-dsl`'s
  `TermArbs` where they fit (add `:test-dsl` to `:theory` `commonTest`); a clause-level `Arb` is local to the spec.
  It runs with the default unificator and a custom one (the existing `*UnificatorSpec` pattern), plus hand-picked
  regression cases next to it.
- **`TheoryCacheConsistencySpec`** (issue §13): parameterized leaves of the form *build, read view
  (`clauses`/`rules`/`directives`/`get`), mutate (`assertA`/`assertZ`/`retract`/`retractAll`), read again, expect
  the change to be visible*. Cases cover facts, directives and nested-arg0 clauses, on the mutable indexed impl.
  A failure here is a bug that gets fixed **before** Step 3.
- **`TheoryLazinessSpec`**: a `CountingUnificator` wraps the default one and counts `match` calls. It asserts that
  `get(q).first()` performs ≤ the matches needed to find the first result, for every impl. This is the executable
  form of the issue's laziness and merge acceptance criteria. It's added red-to-green in Step 1/5, not in Step 0.
- **`TheoryChurnSpec`** (Step 4): after N assert/retract cycles with fresh keys, an `internal` topology count on
  `RootNode` (reachable index nodes and buckets, test-only, `internal`) stays O(live clauses).

## 4. PR sequence

Each step is one PR, `refactor(theory)`/`perf(theory)` per conventional commits, with the `compare.py` table for its
benchmark subset. Steps 1–5 are independent once Step 0 lands, but each needs a re-baseline on rebase.

| Step | PR content | Files | Bench subset | Risk |
|---|---|---|---|---|
| **0** | Benchmarks + workloads + baseline JSON; equivalence/cache specs | `examples/src/benchmarks/.../theory/*`, `workloads.pl`, `theory/src/commonTest/...`, `theory/build.gradle.kts` (test-dsl) | all (baseline) | none (no main code) |
| **1** | Lazy `AbstractListedTheory.get`, `DirectiveIndex.get`, `ListedTheory.retract*` (find first match lazily, then build the list once); single-pass `indexOfFirst`+`removeAt` via `MutableIterator.remove`; iterative `nestedFirstArgument`/`functorOf…`/`arityOf…` (drop `firstArguments` + `takeFirstAfterSkipping` and both `PlatformSpecificUtils` actuals if unused); `MutableIndexedTheory.clauses` → `Iterable { queue.iterator() }` (check callers relying on the snapshot under concurrent modification first: `grep -rn "\.clauses" solve*/`); validate during ingestion (`checkClauseCorrect` inside the `RootNode` insert loop); **delete `rete/generic/**`** | `theory/impl/*`, `rete/custom/Utils.kt`, `rete/custom/leaf/*` | Lookup, Construction | low |
| **2** | `IndexedTheory.retract/retractAll` operate on `queue` directly (one `deepCopy`); `retract(Iterable)` copies once into a `MutableClauseQueue`, retracts all, and **freezes** the result rather than wrapping a mutable queue | `IndexedTheory.kt`, `ReteClauseQueue.kt` | Mutation (immutable) | low |
| **3** | `:theory`-internal bucket type (`ArrayList`-backed; `assertA` = insert at 0 (`System.arraycopy`, cheap at bucket sizes), or `ArrayDeque` if `assertA` benchmarks regress). `utils.dequeOf` stays untouched | `rete/custom/leaf/*`, `DirectiveIndex`, `ZeroArityReteNode` | Lookup, Mutation, Iteration | low |
| **4** | O(1) `size`/`isEmpty`: a counter in `RootNode`, inc/dec on assert/retract (drop `IndexedTheory.sizeCache`). Pruning: retraction returns "now empty" up the path, and parents `remove(key)` | `RootNode`, `nodes/*`, `leaf/*` | Churn, Iteration(size) | medium (ordered/unordered both) |
| **5** | Heap-based lazy k-way merge in `utils.merge` (internal binary heap over the K heads, ties broken by input index to stay stable). Callers: check `grep -rn "merge(" --include=*.kt` for `utils.merge` users before changing semantics; else put it in `:theory` | `utils/.../IterUtils.kt` (+ spec) | Merge, Iteration, Lookup(all-vars) | low |
| **6** | **Checkpoint**: full rerun, README section, decide 7–9 from the numbers | `examples/benchmarks/` | all | — |
| **7** | Multi-argument indexing: per-predicate `PredicateIndex` with arg-0 index as today plus **lazy** secondary arg-i indexes (Strategy C: built on the first query that binds arg i while arg 0 is unbound, for predicates over a size threshold, then maintained on assert/retract). Candidates = bucket(value) ∪ bucket(var), merged by clause order (Step 5 merge); final check is still `unificator.match` | `rete/custom/nodes/*` (new `PredicateIndex`) | Lookup(arg1/last-bound), `fact-table-arg1`, Mutation (insert overhead) | high |
| **8** | Persistent immutable index (path copying). **Only if** Step 6 shows immutable updates matter for a real caller: they're off the `:solve-prolog` hot path (§1) | — | Construction(incremental), Mutation(immutable) | high |
| **9** | Cache redesign (`traversedCacheables + traversed` list copying → parent-linked path or generation counter) **only if** Step 6 allocation profiles show it | `IndexedClause`, `SituatedIndexedClause`, `Cacheable` | Construction, Mutation (B/op) | medium |

Step 7's selection heuristic: the bound argument with the smallest bucket (sizes are O(1) after Step 4); ties go to
the lowest argument index. Strategy A/B/C/D trade-offs get measured within Step 7 on `fact-table-arg1` and
`TheoryMutationBenchmark` (insert cost) before one is kept.

## 5. Definition of done (mapped to the issue)

- Benchmark suite + baseline/checkpoint/final JSON in `examples/benchmarks/results/`, with a README section (Steps 0, 6, final).
- Equivalence, cache and laziness specs are green on JVM + JS (`:theory:jvmTest`, `:theory:jsNodeTest`) (Steps 0, 1, 5).
- `firstOrNull` costs bounded `match` calls on every impl (`TheoryLazinessSpec`) (Steps 1, 5).
- One rebuild per immutable retract (Step 2). Per-bucket `ArrayList` (Step 3). O(1) size, pruned topology (Step 4).
  Heap merge (Step 5).
- `fact-table-arg1` inspects ≈ bucket size, measured as `match` calls in a spec plus the solver benchmark (Step 7).
- Immutable update without a full rebuild (Step 8), or a documented decision, with numbers, that it isn't worth it.

## Open question

JS benchmarks: JVM-only (default above), or also a KMP benchmark module for JS (about one extra PR, and runs
roughly double)?
