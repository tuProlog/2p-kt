# Resolution-timing benchmarks (#1012)

JMH benchmarks measuring how timeout handling affects resolution, in `examples/src/benchmarks`:

- `PrologSolverBenchmark`: throughput of `:solve-prolog` solving each workload of
  `src/benchmarks/resources/.../workloads.pl` to exhaustion, with no timeout (`none`) and with a timeout that never
  fires (`large`, 1 hour), so the difference is the cost of checking it.
- `TimeoutOvershootBenchmark`: time `:solve-prolog` takes to stop a never-ending query (`loop`) under a 10 ms and a
  100 ms timeout. Score minus timeout = how late expiration is detected.

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

## Results

Stored in `results/`, named `<date>-<label>-<commit>.json` (raw JMH JSON).

### Baseline: `2026-11-03-baseline-52348df`

Before #1012: every state transition reads `System.currentTimeMillis()` (timeout or not).
Cloud container, Intel Xeon @ 2.10GHz, 4 vCPUs, JDK 21.0.12.1, JMH 1.37.

| Benchmark | Workload | Timeout | Score | Error | Unit | Allocated (B/op) |
|---|---|---|---:|---:|---|---:|
| PrologSolverBenchmark.solveAll | tiny | none | 740613.082 | 105507.208 | ops/s | 3,848 |
| PrologSolverBenchmark.solveAll | deep-recursion | none | 17.071 | 3.548 | ops/s | 119,410,831 |
| PrologSolverBenchmark.solveAll | nrev | none | 28.723 | 7.197 | ops/s | 57,310,512 |
| PrologSolverBenchmark.solveAll | backtracking | none | 1.506 | 0.291 | ops/s | 985,642,649 |
| PrologSolverBenchmark.solveAll | failing-search | none | 10.338 | 4.138 | ops/s | 184,805,556 |
| PrologSolverBenchmark.solveAll | nested-solving | none | 16.860 | 1.623 | ops/s | 109,299,182 |
| PrologSolverBenchmark.solveAll | primitives | none | 47.366 | 6.333 | ops/s | 39,552,158 |
| PrologSolverBenchmark.solveAll | tiny | large | 714440.706 | 104837.426 | ops/s | 3,888 |
| PrologSolverBenchmark.solveAll | deep-recursion | large | 16.619 | 3.495 | ops/s | 121,219,240 |
| PrologSolverBenchmark.solveAll | nrev | large | 28.114 | 4.554 | ops/s | 56,616,769 |
| PrologSolverBenchmark.solveAll | backtracking | large | 1.510 | 0.175 | ops/s | 995,764,585 |
| PrologSolverBenchmark.solveAll | failing-search | large | 9.987 | 3.536 | ops/s | 185,220,402 |
| PrologSolverBenchmark.solveAll | nested-solving | large | 17.249 | 4.797 | ops/s | 109,186,910 |
| PrologSolverBenchmark.solveAll | primitives | large | 49.486 | 11.234 | ops/s | 40,575,579 |
| TimeoutOvershootBenchmark.loopUntilTimeout | - | 10 ms | 11.121 | 0.074 | ms/op | 31,866,024 |
| TimeoutOvershootBenchmark.loopUntilTimeout | - | 100 ms | 103.343 | 4.903 | ms/op | 238,630,934 |

Clock reads per resolution are not measured here: the baseline has no instrumentation point. Its solver reads the clock
once per state transition, plus once per primitive call.
