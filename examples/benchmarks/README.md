# Resolution benchmarks (#1012, #1019, #1020)

JMH benchmarks in `examples/src/benchmarks`. Timing (#1012) measures how timeout handling affects resolution:

- `PrologSolverBenchmark`: throughput of `:solve-prolog` solving each workload of
  `src/benchmarks/resources/.../workloads.pl` to exhaustion, with no timeout (`none`) and with a timeout that never
  fires (`large`, 1 hour), so the difference is the cost of checking it.
- `TimeoutOvershootBenchmark`: time `:solve-prolog` takes to stop a never-ending query (`loop`) under a 10 ms and a
  100 ms timeout. Score minus timeout = how late expiration is detected.

Both take a `chronometer` parameter (`direct`, `adaptive`; see `ChronometerFactory`), absent from the baseline.
`./gradlew :examples:clockReadsReport` prints deterministic counts instead: resolution steps (leaps), forced clock
checks, and clock reads per workload and chronometer.

`:solve-concurrent` is not benchmarked: it overflows the stack (or hangs) even on `nrev`, and leaks thread pools across
resolutions, independently of timing.

## Running

```
./gradlew :examples:timingBenchmarks                          # full run, GC profiler on; JSON in examples/build/reports/benchmarks/jmh.json
./gradlew :examples:timingBenchmarks -PjmhArgs='-wi 0 -i 1 -r 100ms TimeoutOvershoot'  # quick check of a subset
./gradlew :examples:benchmarksSmokeBenchmark                  # kotlinx-benchmark smoke run, no profiler
```

Defaults: 1 fork, 3 × 2 s warm-up, 5 × 2 s measurement iterations. To compare two runs, run
`python3 examples/benchmarks/compare.py <before.json> <after.json>`.

Unification (#1020) compares the equation-based engine (`AbstractUnificator`, the default before #1020) with the
worklist-based one (`AbstractWorklistUnificator`), both strict, through the `engine` parameter:

- `UnificationBenchmark`: throughput of one `mgu` or `merge` per scenario (see `UnificationScenario`), with and
  without occurs-check. Structures have 1000 arguments, lists 10,000 elements, deep terms 1000 levels; scenarios on
  which the equation-based engine is cubic (variable-heavy ones and merges) use 200 variables.
- `UnificatorSolverBenchmark`: throughput of `:solve-prolog` solving the workloads above with either engine.

```
./gradlew :examples:timingBenchmarks -PjmhArgs='-wi 2 -i 3 -w 1s -r 1s -f 1 UnificationBenchmark'
./gradlew :examples:timingBenchmarks -PjmhArgs='-wi 3 -i 5 -w 2s -r 2s -f 1 UnificatorSolverBenchmark'
python3 examples/benchmarks/engines.py examples/build/reports/benchmarks/jmh.json   # one row per engine pair
```

Clause storage (#1019) is benchmarked in `src/benchmarks/kotlin/.../theory`, each over an `impl` parameter
(`immutable` for `Theory`, `mutable` for `MutableTheory`) and a theory size:

- `TheoryConstructionBenchmark`, `TheoryIncrementalConstructionBenchmark`: building a theory in one go, or by
  repeated `assertZ`.
- `TheoryLookupBenchmark`: first and all clauses matching queries bound on different arguments (see `TheoryQuery`).
- `TheoryMutationBenchmark`: `assertA`, `assertZ`, `retract` of the first and last clause, `retractAll`, `abolish`
  (on mutable theories, each followed by its inverse).
- `TheoryChurnBenchmark`: lookups, iteration and `size` after a history of asserts and retracts.
- `TheoryIterationBenchmark`: iteration, `size`, `hashCode` and `equals` over 1 or 1000 predicates.
- `TheorySolverBenchmark`: `:solve-prolog` on fact tables and dynamic predicates (`theory-workloads.pl`).

```
./gradlew :examples:timingBenchmarks -PjmhArgs='-wi 2 -i 3 -w 1s -r 1s -f 1 -p size=1000 benchmarks.theory.TheoryLookupBenchmark[.]'
./gradlew :examples:theoryMatchReport   # unifications attempted per lookup, deterministic
```

For the `develop` baseline, **do not run `size=100000`**: several operations are roughly quadratic and take hours.
Run one class and size at a time up to `size=10000`, then extrapolate the 100k baseline per case as described below.
Measure the branch at 100k normally, and label every projected baseline and speedup as estimated.

## Results

Benchmark data, reports, logs, and run provenance are maintained in
[tuProlog/2p-kt-benchmarks](https://github.com/tuProlog/2p-kt-benchmarks).
See the [results report](https://github.com/tuProlog/2p-kt-benchmarks/blob/main/REPORTS.md), including the
[three-round zero-arity fix check](https://github.com/tuProlog/2p-kt-benchmarks/blob/main/REPORTS.md#zero-arity-fix-check-2026-11-07-1b32e830c-vs-52d49d34b).

Clone the data repository alongside this repository before running the zero-arity campaign:

```sh
git clone git@github.com:tuProlog/2p-kt-benchmarks.git ../2p-kt-benchmarks
examples/benchmarks/zero-arity.sh origin/ccr-0b4a13b3-ejoi9f origin/ccr-0b4a13b3-ejoi9f-zero-arity 3
```

The script writes compressed JSON to `../2p-kt-benchmarks/results/`; set `BENCHMARK_RESULTS_DIR` to use another
existing directory. Commit generated results in the data repository. Both `compare.py` and `engines.py` accept
plain `.json` and compressed `.json.xz` files.
