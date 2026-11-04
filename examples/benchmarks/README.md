# Resolution benchmarks (#1012, #1020)

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

### Unification engines: `2026-11-03-unification-e0762f3`, `2026-11-03-unificators-*`

After #1020, both engines in the same run (`engines.py`; speed-up is worklist over equational throughput).
Cloud container, Intel Xeon @ 2.80GHz, 4 vCPUs, JDK 21, JMH 1.37. The equation-based engine already includes the
removal of its equation → pair → equation round-trip, which made it 1.5–4× faster on variable-heavy scenarios than
before #1020 (compare with the first measurements, in this PR's history).

| Benchmark | Parameters | equational | worklist | Speed-up | equational B/op | worklist B/op |
|---|---|---:|---:|---:|---:|---:|
| UnificationBenchmark | occursCheck=true, scenario=ground-equal | 10757.686 | 29500.880 | 2.74x | 111,649 | 33,904 |
| UnificationBenchmark | occursCheck=true, scenario=early-mismatch | 22201.958 | 59094.623 | 2.66x | 111,528 | 33,688 |
| UnificationBenchmark | occursCheck=true, scenario=late-mismatch | 11640.924 | 29507.171 | 2.53x | 111,529 | 33,688 |
| UnificationBenchmark | occursCheck=true, scenario=many-variables | 2234.261 | 24410.265 | 10.93x | 141,443 | 65,016 |
| UnificationBenchmark | occursCheck=true, scenario=variable-chain | 10.587 | 13299.541 | 1256.23x | 6,299,667 | 71,057 |
| UnificationBenchmark | occursCheck=true, scenario=repeated-variable | 2213.345 | 24115.000 | 10.90x | 141,443 | 65,032 |
| UnificationBenchmark | occursCheck=true, scenario=repeated-variable-failing | 51349.016 | 122639.370 | 2.39x | 32,464 | 7,120 |
| UnificationBenchmark | occursCheck=true, scenario=deep-terms | 11075.069 | 33554.156 | 3.03x | 225,137 | 1,032 |
| UnificationBenchmark | occursCheck=true, scenario=list-equal | 240.312 | 1519.919 | 6.32x | 1,129,909 | 421 |
| UnificationBenchmark | occursCheck=true, scenario=list-with-variables | 224.483 | 1202.501 | 5.36x | 1,183,739 | 29,606 |
| UnificationBenchmark | occursCheck=true, scenario=list-early-mismatch | 1740.841 | 12227711.122 | 7024.02x | 1,129,637 | 200 |
| UnificationBenchmark | occursCheck=true, scenario=list-late-mismatch | 255.594 | 1739.570 | 6.81x | 1,129,643 | 204 |
| UnificationBenchmark | occursCheck=true, scenario=partial-list | 253.365 | 1301.385 | 5.14x | 1,131,059 | 1,088 |
| UnificationBenchmark | occursCheck=true, scenario=occurs-near-start | 12172397.279 | 17349174.487 | 1.43x | 352 | 200 |
| UnificationBenchmark | occursCheck=true, scenario=occurs-near-end | 138772.038 | 363739.069 | 2.62x | 504 | 280 |
| UnificationBenchmark | occursCheck=true, scenario=occurs-nowhere | 258355.498 | 734790.068 | 2.84x | 1,008 | 880 |
| UnificationBenchmark | occursCheck=true, scenario=self-reference | 15218926.018 | 19297528.000 | 1.27x | 288 | 200 |
| UnificationBenchmark | occursCheck=true, scenario=merge-disjoint | 465.935 | 13888.056 | 29.81x | 302,647 | 146,537 |
| UnificationBenchmark | occursCheck=true, scenario=merge-compatible | 0.844 | 7640.000 | 9052.49x | 1,605,719,637 | 186,897 |
| UnificationBenchmark | occursCheck=true, scenario=merge-incompatible | 11.063 | 117159.158 | 10590.29x | 4,567,879 | 18,080 |
| UnificationBenchmark | occursCheck=true, scenario=merge-chain | 633.541 | 37874.786 | 59.78x | 227,444 | 54,832 |
| UnificationBenchmark | occursCheck=false, scenario=ground-equal | 10911.824 | 28141.679 | 2.58x | 111,649 | 33,904 |
| UnificationBenchmark | occursCheck=false, scenario=early-mismatch | 22016.393 | 67585.601 | 3.07x | 111,528 | 33,688 |
| UnificationBenchmark | occursCheck=false, scenario=late-mismatch | 10548.685 | 28798.263 | 2.73x | 111,529 | 33,688 |
| UnificationBenchmark | occursCheck=false, scenario=many-variables | 2255.543 | 25033.628 | 11.10x | 141,539 | 53,832 |
| UnificationBenchmark | occursCheck=false, scenario=variable-chain | 12.352 | 13706.018 | 1109.63x | 6,299,571 | 71,001 |
| UnificationBenchmark | occursCheck=false, scenario=repeated-variable | 2242.947 | 26461.795 | 11.80x | 141,443 | 53,832 |
| UnificationBenchmark | occursCheck=false, scenario=repeated-variable-failing | 49218.268 | 102696.483 | 2.09x | 32,544 | 7,064 |
| UnificationBenchmark | occursCheck=false, scenario=deep-terms | 11575.950 | 28801.064 | 2.49x | 225,177 | 976 |
| UnificationBenchmark | occursCheck=false, scenario=list-equal | 240.765 | 1475.183 | 6.13x | 1,129,909 | 421 |
| UnificationBenchmark | occursCheck=false, scenario=list-with-variables | 224.139 | 1090.893 | 4.87x | 1,189,457 | 24,006 |
| UnificationBenchmark | occursCheck=false, scenario=list-early-mismatch | 1659.072 | 11803013.684 | 7114.22x | 1,129,639 | 200 |
| UnificationBenchmark | occursCheck=false, scenario=list-late-mismatch | 254.090 | 1654.911 | 6.51x | 1,129,643 | 204 |
| UnificationBenchmark | occursCheck=false, scenario=partial-list | 239.593 | 1377.558 | 5.75x | 1,130,805 | 1,023 |
| UnificationBenchmark | occursCheck=false, scenario=occurs-near-start | 4622940.297 | 5496712.984 | 1.19x | 960 | 824 |
| UnificationBenchmark | occursCheck=false, scenario=occurs-near-end | 4527726.760 | 5287329.204 | 1.17x | 984 | 824 |
| UnificationBenchmark | occursCheck=false, scenario=occurs-nowhere | 4745795.112 | 5359352.875 | 1.13x | 960 | 824 |
| UnificationBenchmark | occursCheck=false, scenario=self-reference | 4887630.466 | 5367203.574 | 1.10x | 984 | 824 |
| UnificationBenchmark | occursCheck=false, scenario=merge-disjoint | 17977.678 | 13258.047 | 0.74x | 109,776 | 124,017 |
| UnificationBenchmark | occursCheck=false, scenario=merge-compatible | 0.882 | 8382.711 | 9505.67x | 1,593,577,864 | 179,865 |
| UnificationBenchmark | occursCheck=false, scenario=merge-incompatible | 11.827 | 157615.990 | 13326.56x | 6,691,210 | 6,880 |
| UnificationBenchmark | occursCheck=false, scenario=merge-chain | 21456.298 | 46115.696 | 2.15x | 104,456 | 43,552 |

Scores in ops/s.

Solver workloads (`2026-11-03-unificators-3009ae1`):

| Benchmark | Parameters | equational | worklist | Speed-up | equational B/op | worklist B/op |
|---|---|---:|---:|---:|---:|---:|
| UnificatorSolverBenchmark | workload=tiny | 708470.843 | 705384.979 | 1.00x | 3,544 | 3,544 |
| UnificatorSolverBenchmark | workload=deep-recursion | 13.004 | 21.077 | 1.62x | 112,569,153 | 72,308,781 |
| UnificatorSolverBenchmark | workload=nrev | 21.292 | 36.427 | 1.71x | 53,029,851 | 32,961,645 |
| UnificatorSolverBenchmark | workload=backtracking | 1.140 | 2.462 | 2.16x | 852,613,709 | 429,679,005 |
| UnificatorSolverBenchmark | workload=failing-search | 8.331 | 11.469 | 1.38x | 172,129,636 | 125,108,414 |
| UnificatorSolverBenchmark | workload=nested-solving | 13.424 | 19.417 | 1.45x | 100,158,791 | 70,758,708 |
| UnificatorSolverBenchmark | workload=primitives | 36.075 | 58.591 | 1.62x | 38,923,807 | 24,994,874 |

Scores in ops/s.

Using `Unificator.merge` instead of `Substitution.plus` in the solver's state machines (rule execution, `catch/3`)
was measured too, with the worklist engine (`2026-11-03-unificators-e0762f3-worklist` without it,
`2026-11-03-unificators-3009ae1` with it):

| Workload | `+` (ops/s) | `merge` (ops/s) | Change | `+` B/op | `merge` B/op |
|---|---:|---:|---:|---:|---:|
| tiny | 715765.676 ± 119106.166 | 705384.979 ± 49040.172 | -1.5% | 3,544 | 3,544 |
| deep-recursion | 21.739 ± 2.659 | 21.077 ± 4.065 | -3.0% | 69,411,637 | 72,308,781 |
| nrev | 39.581 ± 4.850 | 36.427 ± 5.585 | -8.0% | 31,984,334 | 32,961,645 |
| backtracking | 2.580 ± 0.274 | 2.462 ± 0.287 | -4.6% | 414,002,975 | 429,679,005 |
| failing-search | 11.995 ± 1.860 | 11.469 ± 2.859 | -4.4% | 118,287,706 | 125,108,414 |
| nested-solving | 19.668 ± 2.210 | 19.417 ± 3.568 | -1.3% | 67,160,636 | 70,758,708 |
| primitives | 57.389 ± 7.892 | 58.591 ± 2.727 | +2.1% | 24,985,328 | 24,994,874 |

**Findings.**

- The worklist engine is faster on every scenario but one, by 1.1–3× when traversing terms is unavoidable (ground
  structures, deep terms, occurs-check), 5–7× on big lists, 10–60× on variable-heavy structures and merges, and by
  three or four orders of magnitude where the equation-based engine is quadratic or cubic (variable chains, merges
  with overlaps, early mismatches in lists, which it fully decomposes first). It allocates less on every scenario but
  `merge-disjoint` without occurs-check, up to 10,000× less.
- `merge-disjoint` without occurs-check is 0.74×: the equation-based engine shortcuts it to `Substitution.plus`,
  which is not idempotent in general, whereas the worklist engine always builds an idempotent result.
- Resolution is 1.4–2.2× faster, allocating 27–50% less (`tiny` is dominated by set-up).
- `merge` brings nothing in the solver's state machines, where the two substitutions never share variables: it
  was 2–8% slower (within error bars, but consistently), so they keep using `Substitution.plus`.
