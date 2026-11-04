# Resolution-timing benchmarks (#1012)

JMH benchmarks measuring how timeout handling affects resolution, in `examples/src/benchmarks`:

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
once per state transition, i.e. as often as the direct chronometer below.

### Chronometers: `2026-11-03-chronometers-0342e8f`

After #1012, compared with the baseline (same machine, same settings; `compare.py`):

| Benchmark | Parameters | Before | After | Change | Unit |
|---|---|---:|---:|---:|---|
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=tiny | 740613.082 | 905910.638 | +22.3% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=deep-recursion | 17.071 | 16.781 | -1.7% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=nrev | 28.723 | 27.322 | -4.9% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=backtracking | 1.506 | 1.505 | -0.0% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=failing-search | 10.338 | 11.107 | +7.4% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=nested-solving | 16.860 | 17.306 | +2.6% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=primitives | 47.366 | 49.931 | +5.4% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=tiny | 714440.706 | 733440.577 | +2.7% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=deep-recursion | 16.619 | 17.331 | +4.3% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=nrev | 28.114 | 27.583 | -1.9% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=backtracking | 1.510 | 1.573 | +4.1% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=failing-search | 9.987 | 10.607 | +6.2% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=nested-solving | 17.249 | 17.425 | +1.0% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=large, workload=primitives | 49.486 | 48.227 | -2.5% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=tiny | 740613.082 | 862047.791 | +16.4% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=deep-recursion | 17.071 | 16.631 | -2.6% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=nrev | 28.723 | 27.190 | -5.3% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=backtracking | 1.506 | 1.493 | -0.9% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=failing-search | 10.338 | 10.275 | -0.6% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=nested-solving | 16.860 | 17.534 | +4.0% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=none, workload=primitives | 47.366 | 50.489 | +6.6% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=tiny | 714440.706 | 732769.403 | +2.6% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=deep-recursion | 16.619 | 16.997 | +2.3% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=nrev | 28.114 | 27.081 | -3.7% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=backtracking | 1.510 | 1.450 | -4.0% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=failing-search | 9.987 | 10.746 | +7.6% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=nested-solving | 17.249 | 17.245 | -0.0% | ops/s |
| PrologSolverBenchmark.solveAll | chronometer=adaptive, timeout=large, workload=primitives | 49.486 | 47.371 | -4.3% | ops/s |
| TimeoutOvershootBenchmark.loopUntilTimeout | chronometer=direct, timeoutMillis=10 | 11.121 | 10.255 | -7.8% | ms/op |
| TimeoutOvershootBenchmark.loopUntilTimeout | chronometer=direct, timeoutMillis=100 | 103.343 | 104.132 | +0.8% | ms/op |
| TimeoutOvershootBenchmark.loopUntilTimeout | chronometer=adaptive, timeoutMillis=10 | 11.121 | 10.254 | -7.8% | ms/op |
| TimeoutOvershootBenchmark.loopUntilTimeout | chronometer=adaptive, timeoutMillis=100 | 103.343 | 101.519 | -1.8% | ms/op |

Clock reads per resolution (`clockReadsReport`, timeout of 1 hour unless stated; adaptive counts depend on the
machine's speed, as readings are planned from measured leap durations):

| Workload | Leaps | Forced checks | Reads (direct) | Reads (adaptive) | Reads (adaptive, no timeout) |
|---|---:|---:|---:|---:|---:|
| tiny | 4 | 1 | 6 | 4 | 1 |
| deep-recursion | 16009 | 1 | 16011 | 288 | 1 |
| nrev | 2384 | 1 | 2386 | 162 | 1 |
| backtracking | 88385 | 4 | 88394 | 787 | 1 |
| failing-search | 42022 | 0 | 42023 | 115 | 1 |
| nested-solving | 20709 | 1101 | 23011 | 2530 | 101 |
| primitives | 7209 | 1 | 7211 | 47 | 1 |

**Findings.**

- Throughput is unchanged within noise (all changes within ±8%, inside JMH's error bars, except `tiny`, which is
  dominated by set-up), for both chronometers, with and without a timeout. Allocations are 1–9% lower, as requests no
  longer carry timestamps.
- The adaptive chronometer reads the clock 15–350× less often than the direct one (in this run) (9× on `nested-solving`, where each
  of the 1100 nested solutions forces a reading before being published), and once per resolution (plus once per nested
  resolution) without a timeout.
- These savings don't show in throughput because a clock read (tens of nanoseconds for `System.nanoTime`) is negligible
  next to a resolution step here (4–8 µs, mostly allocation: ~7 KB per step). They would matter for a much faster engine, or on
  platforms with costly clocks.
- Timeout overshoot is the same for both chronometers (~0.25 ms on 10 ms, within noise on 100 ms), and lower than
  the baseline's 1.1 ms: elapsed time is now measured in nanoseconds on a monotonic clock, rather than in milliseconds
  of wall-clock time.
