# Resolution benchmarks (#1012, #1020, #1075)

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

Recursive terms (#1075): `RecursiveTermsBenchmark` measures building, traversing, substituting, copying, comparing and
hashing lists, tuples and blocks, eager (built from a list) or lazy (from a sequence), per `size` (10, 99, 101, 1000,
100,000) and `ground`. #1075 also adds solver workloads over long non-ground lists (`nrev-100`, `bind-vars`) and STRIPS
planning over list, tuple and block states (`strips-*`). Every workload is checked for its expected outcome before it is
measured.

## Results

Raw JMH JSON, compressed with `xz`, lives in a separate repository, so as not to grow this one:
[tuProlog/2p-kt-benchmarks](https://github.com/tuProlog/2p-kt-benchmarks/tree/main/results), named
`<date>-<label>-<commit>.json.xz` (the headings below name each run). `compare.py` and `engines.py` read `.xz` files as
they are.

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

### Recursive terms: `2026-11-06-full-{before-806dab1,after-d7c1d00}`

The whole suite (every benchmark class, default settings: 1 fork, 3 × 2 s warm-up, 5 × 2 s measurement, GC profiler),
on `develop` before #1075 (`806dab1`, with #1075's benchmark sources copied over) and after it (`d7c1d00`),
alternating the two per benchmark class. Apple M1 (8 cores), 16 GB, JDK 17.0.21, JMH 1.37. Speed-up is after over
before throughput (before over after, for `ms/op`).

Resolution (`PrologSolverBenchmark`, direct chronometer, no timeout; the other chronometer/timeout combinations agree
within a few percent):

| Workload | Before (ops/s) | After (ops/s) | Speed-up | Before B/op | After B/op |
|---|---:|---:|---:|---:|---:|
| tiny | 1,733,488 ± 12,816 | 1,742,488 ± 13,534 | 1.01x | 3.5 k | 3.5 k |
| deep-recursion | 44.251 ± 1.173 | 46.361 ± 1.777 | 1.05x | 71.1 M | 65.8 M |
| nrev | 70.545 ± 1.386 | 69.352 ± 0.517 | 0.98x | 32.3 M | 26.1 M |
| nrev-100 | 0.724 ± 0.292 | 1.307 ± 0.306 | 1.81x | 1,434 M | 867 M |
| bind-vars | 9.563 ± 0.121 | 11.059 ± 0.935 | 1.16x | 197 M | 210 M |
| strips-list | 0.543 ± 0.004 | 0.563 ± 0.004 | 1.04x | 2,224 M | 2,005 M |
| strips-tuple | 0.421 ± 0.008 | 0.438 ± 0.011 | 1.04x | 2,690 M | 2,439 M |
| strips-block | 0.422 ± 0.011 | 0.440 ± 0.006 | 1.04x | 2,702 M | 2,421 M |
| backtracking | 5.203 ± 0.085 | 5.447 ± 0.030 | 1.05x | 420 M | 379 M |
| failing-search | 24.442 ± 0.789 | 25.910 ± 0.328 | 1.06x | 121 M | 111 M |
| nested-solving | 41.414 ± 0.240 | 43.530 ± 0.363 | 1.05x | 68.7 M | 62.7 M |
| primitives | 109.241 ± 6.205 | 119.721 ± 1.673 | 1.10x | 25.3 M | 22.3 M |

`RecursiveTermsBenchmark`, geometric mean of the speed-up over sizes and groundness, per benchmark and kind
(`*`: some 100,000-item cases overflow the stack before #1075, and are left out of the mean):

| Benchmark | list-eager | list-lazy | tuple-eager | tuple-lazy | block-eager | block-lazy |
|---|---:|---:|---:|---:|---:|---:|
| `construct` | 1.98 | 2.11 | 1.38 | 212.27 | 1.42 | 196.21 |
| `constructConsumeOne` | 2.02 | 2.02 | 1.46 | 188.45 | 1.45 | 161.81 |
| `constructConsumeTen` | 1.93 | 2.00 | 1.35 | 21.63 | 1.44 | 22.16 |
| `constructConsumeAll` | 2.09 | 2.02 | 1.40 | 0.64 | 1.33 | 0.65 |
| `applySubstitution` | 2.75 | 2.16 | 2.16* | 0.26* | 1.90* | 0.34* |
| `applySubstitutionTwice` | 2.10* | 1.33 | 2.30* | 0.16* | 1.93* | 0.23* |
| `freshCopy` | 1.34 | 1.87 | 1.58* | 0.29* | 1.19* | 0.32* |
| `sizeFirst` | 1.92 | 2.14 | 1.88 | 0.66 | 2.06 | 0.67 |
| `sizeRepeated` | 1.00 | 5210.50 | 2.52 | 2.50 | 2.52 | 2.51 |
| `sizeEverySuffix` | 2.22 | 1.48 | 90.89 | 0.65 | 94.04 | 0.73 |
| `isGroundFirst` | 2.09 | 2.02 | 1.43 | 9.97 | 1.45 | 8.98 |
| `isGroundRepeated` | 0.88 | 221.71 | 0.86 | 0.87 | 0.86 | 0.88 |
| `equalsIdentical` | 0.99 | 1.00 | 1.01 | 0.99 | 1.00 | 1.02 |
| `equalsIndependent` | 1.57 | 1.85 | 1.12 | 0.57 | 1.15 | 0.60 |
| `equalsLateMismatch` | 1.85 | 1.96 | 1.09 | 0.56 | 1.07 | 0.61 |
| `structurallyEqualsIndependent` | 1.63 | 1.88 | 1.18 | 0.57 | 1.14 | 0.61 |
| `compareIndependent` | 1.07* | 1.16* | 1.04* | 0.93* | 1.04* | 0.93* |
| `hashFirst` | 2.14 | 2.08 | 1.37 | 0.64 | 1.34 | 0.67 |
| **all** | 1.68 | 3.66 | 1.79 | 1.70 | 1.75 | 1.78 |

**Findings.**

- Resolution on the existing workloads is unchanged to 1.10× faster (`nrev`: 0.98–1.13× across settings) and
  allocates 7–19% less. Long non-ground lists gain the
  most: `nrev-100` is 1.8× faster with 40% less allocation. `bind-vars` is 1.16× faster but allocates 6.5% *more*.
- Before #1075, 25 of the 1,080 recursive-term cases overflow the stack at 100,000 items: `compareIndependent` for
  every kind, `applySubstitution`/`freshCopy` on tuples and blocks, `applySubstitutionTwice` also on eager lists.
  After it, all of them run.
- Eager terms are 1.1–2.8× faster almost everywhere, as metadata no longer needs traversal and substitution no longer
  rebuilds unchanged suffixes. Tuples and blocks with a known size answer `size` in O(1) (`sizeEverySuffix`: 90×).
- Lazy tuples and blocks (built from a sequence, eagerly before #1075) are built 160–210× faster and only pay for what
  is read. Reading them in full costs 0.55–0.75× (`constructConsumeAll`, `hashFirst`, `equals*`, `sizeFirst`).
- **Regression: substituting or fresh-copying a *ground* lazy tuple or block** is 5–30× slower than before (e.g.
  `applySubstitution`, 1000 items: 244 k → 20 k ops/s, 56 B → 184 KB per op). Before #1075, the tuple was eager and
  known to be ground, so the substitution returned it unchanged. Now `LazyTupleImpl` substitutes and fresh-copies by mapping its
  cursor, without checking groundness first, so traversing the result rebuilds every item. Non-ground lazy tuples and blocks are not
  affected (1.1–1.6×), and the solver never builds lazy tuples.
- `isGroundRepeated` is ~13% slower on everything but lazy lists (1.36 G → 1.19 G ops/s): a cached value read through
  a nullable field, rather than a `by lazy` holder. That's 0.1 ns per call. Lazy lists, which recomputed it on every
  call before, are 220× faster.
- Unification (`UnificationBenchmark`) is unchanged with the worklist engine (geometric mean 1.01×). The equational
  engine with occurs-check is slower on a few scenarios: `occurs-nowhere` 0.23× (1.56 M → 0.36 M ops/s, same
  allocation), `repeated-variable` and `variable-chain` 0.73×, `list-early-mismatch` 0.81×. Not explained yet. It is
  not the default engine, and `UnificatorSolverBenchmark` with it is 1.10–1.30× faster.
- Timeout overshoot (`TimeoutOvershootBenchmark`) is unchanged.
