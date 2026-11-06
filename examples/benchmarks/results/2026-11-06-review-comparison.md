# Review rerun: `2fac5388f` vs develop `806dab117`

223 measured comparisons; 70 comparisons against extrapolated 100k baseline scores. The 10k lookup, mutation and iteration cases are calibration additions to the declared matrix. Throughput is ops/s; ± is JMH's 99.9% error. Speedup is branch/develop. Allocations are total bytes/op, not peak memory.

The 100k baseline is **estimated**, per case, as `S(10k)^2/S(1k)` (a power law through the nearest two measured sizes). This assumes the same scaling continues for another decade. These are rough projections, without measured confidence intervals; cache effects, GC and changes in dominant costs can invalidate that assumption. Estimated rows are not mixed into the measured geometric means.

Before the no-100k-baseline constraint was applied, bulk construction had completed and a lookup run was started. Those bulk scores and the aborted lookup log are archived separately; neither is used for the estimates or comparison table.

| Benchmark | Measured cases | Measured speedup (geomean; range) | Estimated 100k cases | Estimated speedup (geomean; range) |
|---|---:|---|---:|---|
| `TheoryConstructionBenchmark` | 8 | 9.86×; 4.93–14.4× | 2 | ≈ 3.70×; 3.32–4.13× |
| `TheoryIncrementalConstructionBenchmark` | 6 | 91.87×; 15.27–2.79e+03× | 0 | —; — |
| `TheoryLookupBenchmark` | 108 | 21.48×; 0.76–1.24e+06× | 36 | ≈ 388.98×; 0.90–1.34e+08× |
| `TheoryMutationBenchmark` | 36 | 26.90×; 0.81–1.14e+04× | 12 | ≈ 66.86×; 0.80–2.93e+04× |
| `TheoryChurnBenchmark` | 12 | 46.90×; 1.44–2.83e+06× | 0 | —; — |
| `TheoryIterationBenchmark` | 40 | 58.40×; 0.75–5.13e+04× | 20 | ≈ 116.38×; 0.23–6.52e+05× |
| `TheorySolverBenchmark` | 6 | 11.46×; 1.64–6.98e+04× | 0 | —; — |
| `PrologSolverBenchmark` | 7 | 1.41×; 1.02–1.79× | 0 | —; — |

| Benchmark | Parameters | Develop ops/s | Branch ops/s | Speedup | Develop B/op | Branch B/op |
|---|---|---:|---:|---:|---:|---:|
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=backtracking | 5.305 ± 0.235 | 9.490 ± 2.953 | 1.79× | 408,285,584 | 281,607,653 |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=deep-recursion | 46.492 ± 13.714 | 67.968 ± 13.053 | 1.46× | 69,805,367 | 50,631,131 |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=failing-search | 25.071 ± 6.811 | 32.339 ± 14.186 | 1.29× | 116,921,724 | 93,258,069 |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=nested-solving | 42.089 ± 15.073 | 60.265 ± 1.511 | 1.43× | 67,057,330 | 52,573,814 |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=nrev | 68.413 ± 10.736 | 111.195 ± 73.657 | 1.63× | 31,994,248 | 24,279,685 |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=primitives | 116.230 ± 42.831 | 163.498 ± 18.784 | 1.41× | 24,558,390 | 20,613,075 |
| PrologSolverBenchmark.solveAll | chronometer=direct, timeout=none, workload=tiny | 1,582,804.906 ± 714,734.295 | 1,612,379.553 ± 48,407.137 | 1.02× | 3,488 | 3,488 |
| TheoryChurnBenchmark.churn | history=0, impl=mutable, op=cycle | 515,331.298 ± 2,614,446.318 | 4,018,433.740 ± 1,225,095.191 | 7.8× | 4,816 | 1,579 |
| TheoryChurnBenchmark.churn | history=0, impl=mutable, op=iterate | 62,605.747 ± 10,768.270 | 301,605.503 ± 50,744.133 | 4.82× | 50,304 | 56 |
| TheoryChurnBenchmark.churn | history=0, impl=mutable, op=lookup | 1,110,940.186 ± 230,937.308 | 1,851,030.547 ± 507,997.109 | 1.67× | 3,424 | 1,768 |
| TheoryChurnBenchmark.churn | history=0, impl=mutable, op=size | 80,454.313 ± 24,070.879 | 317,548,846.844 ± 27,650,517.785 | 3.95e+03× | 1,928 | 24 |
| TheoryChurnBenchmark.churn | history=10000, impl=mutable, op=cycle | 507,434.771 ± 668,194.367 | 4,072,253.388 ± 1,839,236.128 | 8.03× | 4,784 | 1,579 |
| TheoryChurnBenchmark.churn | history=10000, impl=mutable, op=iterate | 67,918.686 ± 9,337.645 | 156,322.230 ± 38,987.893 | 2.3× | 50,304 | 88 |
| TheoryChurnBenchmark.churn | history=10000, impl=mutable, op=lookup | 1,154,747.665 ± 376,708.999 | 1,667,008.801 ± 583,712.276 | 1.44× | 3,424 | 1,792 |
| TheoryChurnBenchmark.churn | history=10000, impl=mutable, op=size | 1,367.903 ± 194.603 | 307,970,967.884 ± 41,812,574.135 | 2.25e+05× | 320,451 | 24 |
| TheoryChurnBenchmark.churn | history=100000, impl=mutable, op=cycle | 504,280.138 ± 1,106,770.882 | 3,957,112.638 ± 1,683,174.728 | 7.85× | 4,776 | 1,547 |
| TheoryChurnBenchmark.churn | history=100000, impl=mutable, op=iterate | 66,595.154 ± 11,874.773 | 154,117.484 ± 128,883.683 | 2.31× | 50,304 | 88 |
| TheoryChurnBenchmark.churn | history=100000, impl=mutable, op=lookup | 1,106,449.429 ± 544,614.078 | 1,640,705.690 ± 524,671.452 | 1.48× | 3,440 | 1,976 |
| TheoryChurnBenchmark.churn | history=100000, impl=mutable, op=size | 107.933 ± 16.350 | 305,841,855.800 ± 34,931,942.075 | 2.83e+06× | 16,001,136 | 24 |
| TheoryConstructionBenchmark.bulk | impl=immutable, size=10 | 56,315.402 ± 12,600.354 | 631,580.674 ± 165,372.239 | 11.2× | 87,553 | 4,152 |
| TheoryConstructionBenchmark.bulk | impl=immutable, size=100 | 6,653.089 ± 2,394.993 | 72,959.104 ± 3,426.262 | 11× | 831,553 | 31,512 |
| TheoryConstructionBenchmark.bulk | impl=immutable, size=1000 | 682.360 ± 268.367 | 6,831.738 ± 2,166.059 | 10× | 8,277,921 | 306,441 |
| TheoryConstructionBenchmark.bulk | impl=immutable, size=10000 | 62.923 ± 13.920 | 310.266 ± 269.568 | 4.93× | 82,827,502 | 3,108,294 |
| TheoryConstructionBenchmark.bulk | impl=immutable, size=100000 | ≈ 5.8 (estimated) | 19.268 ± 5.624 | ≈ 3.32× | — | 29,844,570 |
| TheoryConstructionBenchmark.bulk | impl=mutable, size=10 | 55,341.518 ± 11,919.286 | 702,531.299 ± 165,178.745 | 12.7× | 87,416 | 5,360 |
| TheoryConstructionBenchmark.bulk | impl=mutable, size=100 | 6,760.880 ± 2,011.317 | 97,266.098 ± 24,499.323 | 14.4× | 831,401 | 45,072 |
| TheoryConstructionBenchmark.bulk | impl=mutable, size=1000 | 695.535 ± 137.825 | 9,616.198 ± 3,076.253 | 13.8× | 8,261,784 | 440,353 |
| TheoryConstructionBenchmark.bulk | impl=mutable, size=10000 | 61.999 ± 29.504 | 362.646 ± 154.897 | 5.85× | 82,827,368 | 4,557,235 |
| TheoryConstructionBenchmark.bulk | impl=mutable, size=100000 | ≈ 5.53 (estimated) | 22.816 ± 22.085 | ≈ 4.13× | — | 44,812,402 |
| TheoryIncrementalConstructionBenchmark.incremental | impl=immutable, size=10 | 1,473.081 ± 559.051 | 138,625.462 ± 13,369.502 | 94.1× | 2,561,261 | 37,848 |
| TheoryIncrementalConstructionBenchmark.incremental | impl=immutable, size=100 | 12.946 ± 8.712 | 6,616.820 ± 1,810.268 | 511× | 251,707,511 | 1,019,569 |
| TheoryIncrementalConstructionBenchmark.incremental | impl=immutable, size=1000 | 0.034 ± 0.004 | 94.112 ± 18.641 | 2.79e+03× | 21,021,759,504 | 74,988,841 |
| TheoryIncrementalConstructionBenchmark.incremental | impl=mutable, size=10 | 56,775.953 ± 16,551.121 | 1,073,859.850 ± 282,990.603 | 18.9× | 87,448 | 5,192 |
| TheoryIncrementalConstructionBenchmark.incremental | impl=mutable, size=100 | 6,594.037 ± 44.463 | 100,700.746 ± 27,238.645 | 15.3× | 831,305 | 43,824 |
| TheoryIncrementalConstructionBenchmark.incremental | impl=mutable, size=1000 | 664.187 ± 192.870 | 10,299.440 ± 1,307.560 | 15.5× | 8,261,730 | 428,305 |
| TheoryIterationBenchmark.whole | impl=immutable, op=equals, predicates=1, size=1000 | 162,911.179 ± 26,729.396 | 1,060,123.540 ± 267,883.187 | 6.51× | 96 | 0 |
| TheoryIterationBenchmark.whole | impl=immutable, op=equals, predicates=1, size=10000 | 11,234.645 ± 1,159.463 | 90,886.953 ± 24,275.067 | 8.09× | 97 | 0 |
| TheoryIterationBenchmark.whole | impl=immutable, op=equals, predicates=1, size=100000 | ≈ 775 (estimated) | 2,076.282 ± 3,990.198 | ≈ 2.68× | — | 3 |
| TheoryIterationBenchmark.whole | impl=immutable, op=equals, predicates=1000, size=1000 | 120,174.717 ± 13,588.910 | 1,107,442.174 ± 78,000.034 | 9.22× | 96 | 0 |
| TheoryIterationBenchmark.whole | impl=immutable, op=equals, predicates=1000, size=10000 | 11,286.639 ± 2,999.019 | 92,996.917 ± 2,542.464 | 8.24× | 97 | 0 |
| TheoryIterationBenchmark.whole | impl=immutable, op=equals, predicates=1000, size=100000 | ≈ 1.06e+03 (estimated) | 2,078.329 ± 6,120.263 | ≈ 1.96× | — | 3 |
| TheoryIterationBenchmark.whole | impl=immutable, op=hashCode, predicates=1, size=1000 | 242,933,097.029 ± 74,076,414.158 | 182,408,900.239 ± 48,108,677.553 | 0.751× | 16 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=hashCode, predicates=1, size=10000 | 239,402,199.013 ± 44,560,395.037 | 188,126,354.776 ± 51,789,242.173 | 0.786× | 16 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=hashCode, predicates=1, size=100000 | ≈ 2.36e+08 (estimated) | 192,216,588.750 ± 9,548,759.017 | ≈ 0.815× | — | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=hashCode, predicates=1000, size=1000 | 223,978,676.546 ± 483,744,177.839 | 182,478,076.282 ± 41,472,172.680 | 0.815× | 16 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=hashCode, predicates=1000, size=10000 | 240,621,433.553 ± 109,316,961.235 | 189,466,052.035 ± 59,357,916.344 | 0.787× | 16 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=hashCode, predicates=1000, size=100000 | ≈ 2.59e+08 (estimated) | 191,768,511.967 ± 11,327,514.753 | ≈ 0.742× | — | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate, predicates=1, size=1000 | 351,497.162 ± 23,439.373 | 260,476,604.725 ± 81,400,651.211 | 741× | 64 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate, predicates=1, size=10000 | 12,278.637 ± 6,355.536 | 262,412,750.876 ± 77,781,833.335 | 2.14e+04× | 65 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate, predicates=1, size=100000 | ≈ 429 (estimated) | 279,653,591.677 ± 1,980,239.669 | ≈ 6.52e+05× | — | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate, predicates=1000, size=1000 | 350,943.486 ± 96,142.421 | 260,886,802.944 ± 94,416,706.152 | 743× | 64 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate, predicates=1000, size=10000 | 12,802.482 ± 4,241.976 | 258,293,356.820 ± 246,590,605.591 | 2.02e+04× | 65 | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate, predicates=1000, size=100000 | ≈ 467 (estimated) | 277,156,163.713 ± 27,455,529.079 | ≈ 5.93e+05× | — | 16 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate-after-update, predicates=1, size=1000 | 114.482 ± 22.592 | 297,938.734 ± 65,332.185 | 2.6e+03× | 9,992,980 | 24,880 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate-after-update, predicates=1, size=10000 | 1.268 ± 0.839 | 35,733.127 ± 2,049.490 | 2.82e+04× | 818,130,987 | 240,880 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate-after-update, predicates=1, size=100000 | ≈ 0.0141 (estimated) | 1,102.973 ± 279.959 | ≈ 7.85e+04× | — | 2,401,070 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate-after-update, predicates=1000, size=1000 | 92.662 ± 26.807 | 53,107.789 ± 14,504.271 | 573× | 25,537,634 | 53,008 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate-after-update, predicates=1000, size=10000 | 5.012 ± 2.305 | 32,021.423 ± 9,460.639 | 6.39e+03× | 263,906,535 | 161,120 |
| TheoryIterationBenchmark.whole | impl=immutable, op=iterate-after-update, predicates=1000, size=100000 | ≈ 0.271 (estimated) | 1,954.890 ± 534.545 | ≈ 7.21e+03× | — | 1,242,298 |
| TheoryIterationBenchmark.whole | impl=immutable, op=size, predicates=1, size=1000 | 207,186,593.119 ± 8,499,695.297 | 299,642,480.754 ± 7,399,306.683 | 1.45× | 24 | 24 |
| TheoryIterationBenchmark.whole | impl=immutable, op=size, predicates=1, size=10000 | 201,853,860.840 ± 13,233,919.090 | 305,801,575.133 ± 104,655,269.940 | 1.51× | 24 | 24 |
| TheoryIterationBenchmark.whole | impl=immutable, op=size, predicates=1, size=100000 | ≈ 1.97e+08 (estimated) | 318,057,806.891 ± 12,219,435.721 | ≈ 1.62× | — | 24 |
| TheoryIterationBenchmark.whole | impl=immutable, op=size, predicates=1000, size=1000 | 204,012,178.815 ± 75,053,455.466 | 296,441,714.366 ± 108,688,727.520 | 1.45× | 24 | 24 |
| TheoryIterationBenchmark.whole | impl=immutable, op=size, predicates=1000, size=10000 | 210,727,321.340 ± 311,396,574.020 | 310,075,287.988 ± 13,127,036.617 | 1.47× | 24 | 24 |
| TheoryIterationBenchmark.whole | impl=immutable, op=size, predicates=1000, size=100000 | ≈ 2.18e+08 (estimated) | 317,293,798.868 ± 14,345,999.136 | ≈ 1.46× | — | 24 |
| TheoryIterationBenchmark.whole | impl=mutable, op=equals, predicates=1, size=1000 | 87,936.373 ± 19,079.348 | 311,581.791 ± 85,665.895 | 3.54× | 30,144 | 80 |
| TheoryIterationBenchmark.whole | impl=mutable, op=equals, predicates=1, size=10000 | 8,653.616 ± 3,324.886 | 33,538.619 ± 156.479 | 3.88× | 338,033 | 80 |
| TheoryIterationBenchmark.whole | impl=mutable, op=equals, predicates=1, size=100000 | ≈ 852 (estimated) | 769.052 ± 356.619 | ≈ 0.903× | — | 89 |
| TheoryIterationBenchmark.whole | impl=mutable, op=equals, predicates=1000, size=1000 | 88,599.639 ± 6,660.832 | 305,870.800 ± 22,627.381 | 3.45× | 30,144 | 80 |
| TheoryIterationBenchmark.whole | impl=mutable, op=equals, predicates=1000, size=10000 | 8,759.360 ± 2,374.005 | 33,217.973 ± 370.802 | 3.79× | 338,033 | 80 |
| TheoryIterationBenchmark.whole | impl=mutable, op=equals, predicates=1000, size=100000 | ≈ 866 (estimated) | 792.694 ± 163.392 | ≈ 0.915× | — | 89 |
| TheoryIterationBenchmark.whole | impl=mutable, op=hashCode, predicates=1, size=1000 | 90,885.746 ± 24,046.879 | 145,283.196 ± 32,889.560 | 1.6× | 15,088 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=hashCode, predicates=1, size=10000 | 11,059.518 ± 855.089 | 15,355.847 ± 335.291 | 1.39× | 169,033 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=hashCode, predicates=1, size=100000 | ≈ 1.35e+03 (estimated) | 404.939 ± 192.466 | ≈ 0.301× | — | 73 |
| TheoryIterationBenchmark.whole | impl=mutable, op=hashCode, predicates=1000, size=1000 | 91,953.480 ± 29,289.699 | 142,652.623 ± 37,366.784 | 1.55× | 15,088 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=hashCode, predicates=1000, size=10000 | 10,914.743 ± 1,312.473 | 15,534.715 ± 164.071 | 1.42× | 169,033 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=hashCode, predicates=1000, size=100000 | ≈ 1.3e+03 (estimated) | 302.865 ± 78.227 | ≈ 0.234× | — | 79 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate, predicates=1, size=1000 | 186,487.989 ± 58,825.631 | 1,054,846.467 ± 43,132.522 | 5.66× | 15,088 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate, predicates=1, size=10000 | 19,840.084 ± 4,458.377 | 100,262.144 ± 26,354.856 | 5.05× | 169,032 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate, predicates=1, size=100000 | ≈ 2.11e+03 (estimated) | 5,880.563 ± 49,157.168 | ≈ 2.79× | — | 57 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate, predicates=1000, size=1000 | 186,962.504 ± 51,899.419 | 1,053,113.862 ± 382,416.180 | 5.63× | 15,088 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate, predicates=1000, size=10000 | 19,723.246 ± 4,233.822 | 100,327.177 ± 11,604.004 | 5.09× | 169,032 | 56 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate, predicates=1000, size=100000 | ≈ 2.08e+03 (estimated) | 5,719.357 ± 17,320.198 | ≈ 2.75× | — | 57 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate-after-update, predicates=1, size=1000 | 125.091 ± 39.523 | 357,809.227 ± 108,874.666 | 2.86e+03× | 8,158,553 | 964 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate-after-update, predicates=1, size=10000 | 1.553 ± 0.291 | 45,267.357 ± 62,495.011 | 2.91e+04× | 800,407,683 | 1,018 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate-after-update, predicates=1, size=100000 | ≈ 0.0193 (estimated) | 5,452.350 ± 10,440.245 | ≈ 2.83e+05× | — | 825 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate-after-update, predicates=1000, size=1000 | 123.047 ± 44.267 | 403,306.838 ± 117,090.294 | 3.28e+03× | 18,964,408 | 782 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate-after-update, predicates=1000, size=10000 | 4.536 ± 0.902 | 44,777.073 ± 84,501.212 | 9.87e+03× | 686,057,673 | 1,018 |
| TheoryIterationBenchmark.whole | impl=mutable, op=iterate-after-update, predicates=1000, size=100000 | ≈ 0.167 (estimated) | 5,316.141 ± 6,889.473 | ≈ 3.18e+04× | — | 817 |
| TheoryIterationBenchmark.whole | impl=mutable, op=size, predicates=1, size=1000 | 143,216.095 ± 13,520.030 | 314,962,504.979 ± 24,402,747.449 | 2.2e+03× | 528 | 24 |
| TheoryIterationBenchmark.whole | impl=mutable, op=size, predicates=1, size=10000 | 13,668.357 ± 1,038.379 | 324,485,184.499 ± 49,511,166.645 | 2.37e+04× | 529 | 24 |
| TheoryIterationBenchmark.whole | impl=mutable, op=size, predicates=1, size=100000 | ≈ 1.3e+03 (estimated) | 320,686,699.192 ± 78,370,138.425 | ≈ 2.46e+05× | — | 24 |
| TheoryIterationBenchmark.whole | impl=mutable, op=size, predicates=1000, size=1000 | 16,332.074 ± 5,431.324 | 314,866,939.761 ± 9,446,199.441 | 1.93e+04× | 32,024 | 24 |
| TheoryIterationBenchmark.whole | impl=mutable, op=size, predicates=1000, size=10000 | 6,384.892 ± 865.298 | 327,469,414.694 ± 18,658,171.499 | 5.13e+04× | 184,057 | 24 |
| TheoryIterationBenchmark.whole | impl=mutable, op=size, predicates=1000, size=100000 | ≈ 2.5e+03 (estimated) | 321,471,412.214 ± 67,039,960.431 | ≈ 1.29e+05× | — | 24 |
| TheoryLookupBenchmark.all | impl=immutable, query=all-vars, size=10 | 122,537.730 ± 14,001.046 | 201,164.161 ± 55,220.365 | 1.64× | 25,264 | 15,168 |
| TheoryLookupBenchmark.all | impl=immutable, query=all-vars, size=1000 | 126.541 ± 58.761 | 2,248.046 ± 89.178 | 17.8× | 8,474,918 | 1,488,395 |
| TheoryLookupBenchmark.all | impl=immutable, query=all-vars, size=10000 | 1.277 ± 0.447 | 194.294 ± 38.223 | 152× | 2,004,674,227 | 14,880,428 |
| TheoryLookupBenchmark.all | impl=immutable, query=all-vars, size=100000 | ≈ 0.0129 (estimated) | 20.696 ± 5.922 | ≈ 1.61e+03× | — | 148,800,717 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg0-bound, size=10 | 1,176,690.427 ± 116,865.738 | 1,953,931.760 ± 490,515.281 | 1.66× | 3,424 | 1,544 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg0-bound, size=1000 | 1,169,867.456 ± 1,036,419.838 | 2,065,700.197 ± 664,092.120 | 1.77× | 3,496 | 1,592 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg0-bound, size=10000 | 1,133,436.300 ± 470,685.066 | 1,777,661.603 ± 52,505.932 | 1.57× | 3,304 | 1,672 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg0-bound, size=100000 | ≈ 1.1e+06 (estimated) | 1,622,768.806 ± 5,295,954.642 | ≈ 1.48× | — | 1,672 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg1-bound, size=10 | 218,383.480 ± 74,988.135 | 2,003,483.163 ± 435,361.674 | 9.17× | 12,640 | 1,544 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg1-bound, size=1000 | 134.141 ± 77.647 | 117,932.975 ± 29,814.361 | 879× | 7,021,339 | 25,744 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg1-bound, size=10000 | 1.491 ± 0.084 | 10,345.620 ± 4,723.183 | 6.94e+03× | 789,093,373 | 254,809 |
| TheoryLookupBenchmark.all | impl=immutable, query=arg1-bound, size=100000 | ≈ 0.0166 (estimated) | 1,042.893 ± 400.731 | ≈ 6.29e+04× | — | 2,544,415 |
| TheoryLookupBenchmark.all | impl=immutable, query=joint-args, size=10 | 1,014,016.359 ± 264,071.122 | 1,379,591.782 ± 235,923.237 | 1.36× | 3,384 | 1,496 |
| TheoryLookupBenchmark.all | impl=immutable, query=joint-args, size=1000 | 172,651.299 ± 11,302.121 | 904,844.668 ± 34,366.622 | 5.24× | 9,536 | 1,976 |
| TheoryLookupBenchmark.all | impl=immutable, query=joint-args, size=10000 | 64,381.917 ± 13,134.299 | 277,147.884 ± 87,902.765 | 4.3× | 22,656 | 4,616 |
| TheoryLookupBenchmark.all | impl=immutable, query=joint-args, size=100000 | ≈ 2.4e+04 (estimated) | 140,036.645 ± 41,019.982 | ≈ 5.83× | — | 6,504 |
| TheoryLookupBenchmark.all | impl=immutable, query=last-bound, size=10 | 201,002.616 ± 78,487.654 | 2,035,824.367 ± 871,050.203 | 10.1× | 13,504 | 1,544 |
| TheoryLookupBenchmark.all | impl=immutable, query=last-bound, size=1000 | 139.149 ± 19.589 | 2,105,981.260 ± 46,693.371 | 1.51e+04× | 7,092,337 | 1,608 |
| TheoryLookupBenchmark.all | impl=immutable, query=last-bound, size=10000 | 1.529 ± 0.226 | 1,867,699.500 ± 891,969.309 | 1.22e+06× | 789,755,637 | 1,576 |
| TheoryLookupBenchmark.all | impl=immutable, query=last-bound, size=100000 | ≈ 0.0168 (estimated) | 1,935,902.775 ± 156,735.255 | ≈ 1.15e+08× | — | 1,672 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg0, size=10 | 653,608.764 ± 95,669.721 | 1,776,992.651 ± 2,041,945.289 | 2.72× | 6,712 | 1,280 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg0, size=1000 | 618,196.017 ± 166,518.807 | 2,085,273.970 ± 935,357.113 | 3.37× | 6,824 | 1,296 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg0, size=10000 | 595,201.292 ± 386,300.572 | 1,875,422.252 ± 239,957.969 | 3.15× | 6,952 | 1,384 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg0, size=100000 | ≈ 5.73e+05 (estimated) | 2,054,367.522 ± 297,791.950 | ≈ 3.58× | — | 1,296 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg1, size=10 | 199,436.502 ± 13,764.755 | 1,682,354.298 ± 503,913.651 | 8.44× | 13,280 | 1,568 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg1, size=1000 | 3,490.691 ± 865.041 | 1,695,718.743 ± 552,520.343 | 486× | 790,858 | 1,784 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg1, size=10000 | 328.987 ± 35.095 | 1,651,215.719 ± 418,603.978 | 5.02e+03× | 6,646,893 | 1,488 |
| TheoryLookupBenchmark.all | impl=immutable, query=nested-arg1, size=100000 | ≈ 31 (estimated) | 1,700,236.224 ± 541,631.571 | ≈ 5.48e+04× | — | 1,488 |
| TheoryLookupBenchmark.all | impl=immutable, query=no-match, size=10 | 4,568,773.497 ± 1,630,626.806 | 41,499,535.717 ± 7,797,781.802 | 9.08× | 1,552 | 64 |
| TheoryLookupBenchmark.all | impl=immutable, query=no-match, size=1000 | 4,594,410.803 ± 1,497,313.580 | 30,929,674.027 ± 298,070.116 | 6.73× | 1,520 | 112 |
| TheoryLookupBenchmark.all | impl=immutable, query=no-match, size=10000 | 4,600,720.438 ± 1,433,452.488 | 25,169,712.710 ± 1,248,861.374 | 5.47× | 1,408 | 112 |
| TheoryLookupBenchmark.all | impl=immutable, query=no-match, size=100000 | ≈ 4.61e+06 (estimated) | 27,904,665.166 ± 20,434,254.384 | ≈ 6.06× | — | 112 |
| TheoryLookupBenchmark.all | impl=immutable, query=zero-arity, size=10 | 615,347.312 ± 149,856.775 | 524,280.660 ± 69,625.955 | 0.852× | 9,232 | 9,232 |
| TheoryLookupBenchmark.all | impl=immutable, query=zero-arity, size=1000 | 5,958.194 ± 1,051.431 | 5,556.064 ± 1,467.486 | 0.933× | 912,433 | 912,169 |
| TheoryLookupBenchmark.all | impl=immutable, query=zero-arity, size=10000 | 542.298 ± 165.697 | 503.007 ± 178.071 | 0.928× | 8,800,421 | 8,800,230 |
| TheoryLookupBenchmark.all | impl=immutable, query=zero-arity, size=100000 | ≈ 49.4 (estimated) | 47.634 ± 15.579 | ≈ 0.965× | — | 88,000,360 |
| TheoryLookupBenchmark.all | impl=mutable, query=all-vars, size=10 | 118,741.501 ± 26,380.808 | 213,969.834 ± 60,593.325 | 1.8× | 25,848 | 15,224 |
| TheoryLookupBenchmark.all | impl=mutable, query=all-vars, size=1000 | 129.994 ± 23.584 | 2,207.251 ± 71.381 | 17× | 8,410,917 | 1,488,386 |
| TheoryLookupBenchmark.all | impl=mutable, query=all-vars, size=10000 | 1.507 ± 0.470 | 190.285 ± 80.892 | 126× | 804,314,232 | 14,880,476 |
| TheoryLookupBenchmark.all | impl=mutable, query=all-vars, size=100000 | ≈ 0.0175 (estimated) | 20.411 ± 1.728 | ≈ 1.17e+03× | — | 148,800,770 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg0-bound, size=10 | 1,190,956.803 ± 477,959.737 | 1,852,016.540 ± 484,777.240 | 1.56× | 3,424 | 1,720 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg0-bound, size=1000 | 1,108,324.620 ± 261,126.406 | 2,046,119.406 ± 498,679.910 | 1.85× | 3,424 | 1,656 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg0-bound, size=10000 | 1,080,777.538 ± 802,461.035 | 1,957,552.675 ± 251,105.029 | 1.81× | 3,416 | 1,728 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg0-bound, size=100000 | ≈ 1.05e+06 (estimated) | 1,978,840.884 ± 96,838.387 | ≈ 1.88× | — | 1,656 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg1-bound, size=10 | 219,437.284 ± 74,737.601 | 1,888,768.042 ± 537,353.979 | 8.61× | 12,640 | 1,720 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg1-bound, size=1000 | 141.711 ± 53.086 | 116,565.061 ± 37,241.294 | 823× | 7,021,336 | 25,920 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg1-bound, size=10000 | 1.498 ± 0.178 | 11,725.381 ± 3,420.690 | 7.83e+03× | 789,093,373 | 254,857 |
| TheoryLookupBenchmark.all | impl=mutable, query=arg1-bound, size=100000 | ≈ 0.0158 (estimated) | 1,103.377 ± 164.675 | ≈ 6.97e+04× | — | 2,544,486 |
| TheoryLookupBenchmark.all | impl=mutable, query=joint-args, size=10 | 1,012,162.904 ± 38,767.192 | 1,517,320.413 ± 361,359.079 | 1.5× | 3,384 | 1,512 |
| TheoryLookupBenchmark.all | impl=mutable, query=joint-args, size=1000 | 175,729.063 ± 79,150.435 | 913,638.557 ± 232,574.492 | 5.2× | 9,512 | 2,128 |
| TheoryLookupBenchmark.all | impl=mutable, query=joint-args, size=10000 | 57,126.116 ± 22,840.070 | 309,662.406 ± 82,319.439 | 5.42× | 22,928 | 4,728 |
| TheoryLookupBenchmark.all | impl=mutable, query=joint-args, size=100000 | ≈ 1.86e+04 (estimated) | 120,778.136 ± 10,924.879 | ≈ 6.5× | — | 6,760 |
| TheoryLookupBenchmark.all | impl=mutable, query=last-bound, size=10 | 206,056.091 ± 23,682.707 | 1,921,712.994 ± 581,503.977 | 9.33× | 13,504 | 1,720 |
| TheoryLookupBenchmark.all | impl=mutable, query=last-bound, size=1000 | 140.420 ± 8.358 | 1,876,293.274 ± 633,520.445 | 1.34e+04× | 7,092,337 | 1,768 |
| TheoryLookupBenchmark.all | impl=mutable, query=last-bound, size=10000 | 1.396 ± 0.503 | 1,727,490.603 ± 603,840.406 | 1.24e+06× | 789,755,600 | 1,864 |
| TheoryLookupBenchmark.all | impl=mutable, query=last-bound, size=100000 | ≈ 0.0139 (estimated) | 1,852,508.819 ± 1,048,905.464 | ≈ 1.34e+08× | — | 1,728 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg0, size=10 | 671,286.836 ± 191,516.290 | 2,079,231.365 ± 683,125.552 | 3.1× | 6,776 | 1,232 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg0, size=1000 | 588,101.641 ± 222,376.069 | 1,958,625.749 ± 600,881.609 | 3.33× | 6,856 | 1,456 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg0, size=10000 | 648,189.345 ± 223,893.108 | 2,043,204.481 ± 860,982.794 | 3.15× | 6,088 | 1,448 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg0, size=100000 | ≈ 7.14e+05 (estimated) | 2,125,994.576 ± 480,831.057 | ≈ 2.98× | — | 1,448 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg1, size=10 | 203,692.731 ± 53,210.487 | 1,769,967.749 ± 518,902.977 | 8.69× | 13,280 | 1,832 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg1, size=1000 | 3,539.961 ± 483.534 | 1,807,520.872 ± 376,429.789 | 511× | 790,858 | 1,880 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg1, size=10000 | 303.699 ± 110.136 | 1,694,015.390 ± 488,025.699 | 5.58e+03× | 8,167,199 | 1,768 |
| TheoryLookupBenchmark.all | impl=mutable, query=nested-arg1, size=100000 | ≈ 26.1 (estimated) | 1,571,431.676 ± 580,545.908 | ≈ 6.03e+04× | — | 1,768 |
| TheoryLookupBenchmark.all | impl=mutable, query=no-match, size=10 | 4,664,924.049 ± 6,565.145 | 46,736,400.099 ± 11,485,090.573 | 10× | 1,552 | 64 |
| TheoryLookupBenchmark.all | impl=mutable, query=no-match, size=1000 | 4,537,290.446 ± 317,770.089 | 33,545,448.980 ± 1,818,193.027 | 7.39× | 1,520 | 112 |
| TheoryLookupBenchmark.all | impl=mutable, query=no-match, size=10000 | 4,227,949.838 ± 182,807.485 | 29,911,206.736 ± 2,433,335.362 | 7.07× | 1,520 | 112 |
| TheoryLookupBenchmark.all | impl=mutable, query=no-match, size=100000 | ≈ 3.94e+06 (estimated) | 29,521,306.023 ± 4,626,714.839 | ≈ 7.49× | — | 112 |
| TheoryLookupBenchmark.all | impl=mutable, query=zero-arity, size=10 | 563,903.298 ± 118,641.366 | 534,367.472 ± 4,442.675 | 0.948× | 9,552 | 9,264 |
| TheoryLookupBenchmark.all | impl=mutable, query=zero-arity, size=1000 | 6,915.824 ± 1,796.885 | 5,286.962 ± 249.961 | 0.764× | 880,433 | 912,217 |
| TheoryLookupBenchmark.all | impl=mutable, query=zero-arity, size=10000 | 532.944 ± 167.042 | 470.397 ± 159.279 | 0.883× | 9,120,421 | 8,800,303 |
| TheoryLookupBenchmark.all | impl=mutable, query=zero-arity, size=100000 | ≈ 41.1 (estimated) | 48.509 ± 18.034 | ≈ 1.18× | — | 88,000,404 |
| TheoryLookupBenchmark.first | impl=immutable, query=all-vars, size=10 | 577,364.798 ± 291,115.147 | 1,816,913.250 ± 524,664.474 | 3.15× | 6,440 | 1,800 |
| TheoryLookupBenchmark.first | impl=immutable, query=all-vars, size=1000 | 16,500.030 ± 4,332.196 | 1,814,588.787 ± 456,416.083 | 110× | 248,659 | 1,848 |
| TheoryLookupBenchmark.first | impl=immutable, query=all-vars, size=10000 | 1,628.893 ± 568.252 | 1,696,268.202 ± 462,862.204 | 1.04e+03× | 2,500,821 | 1,848 |
| TheoryLookupBenchmark.first | impl=immutable, query=all-vars, size=100000 | ≈ 161 (estimated) | 1,692,130.130 ± 437,879.676 | ≈ 1.05e+04× | — | 1,848 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg0-bound, size=10 | 1,261,769.219 ± 404,887.395 | 2,113,886.860 ± 63,541.039 | 1.68× | 3,424 | 1,584 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg0-bound, size=1000 | 1,156,008.926 ± 158,099.441 | 2,102,588.727 ± 60,871.485 | 1.82× | 3,472 | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg0-bound, size=10000 | 1,096,303.819 ± 289,174.193 | 1,764,408.130 ± 272,794.374 | 1.61× | 3,472 | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg0-bound, size=100000 | ≈ 1.04e+06 (estimated) | 1,851,770.285 ± 1,164,621.148 | ≈ 1.78× | — | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg1-bound, size=10 | 261,246.225 ± 60,019.965 | 2,103,672.317 ± 575,103.351 | 8.05× | 11,344 | 1,584 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg1-bound, size=1000 | 6,070.097 ± 1,594.636 | 2,110,373.636 ± 583,566.479 | 348× | 350,465 | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg1-bound, size=10000 | 620.437 ± 210.659 | 1,766,339.937 ± 374,122.675 | 2.85e+03× | 3,610,868 | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=arg1-bound, size=100000 | ≈ 63.4 (estimated) | 1,773,001.087 ± 378,584.254 | ≈ 2.8e+04× | — | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=joint-args, size=10 | 1,282,029.419 ± 431,051.588 | 1,582,982.701 ± 97,356.600 | 1.23× | 3,184 | 1,520 |
| TheoryLookupBenchmark.first | impl=immutable, query=joint-args, size=1000 | 322,610.745 ± 99,606.396 | 1,479,099.651 ± 391,436.600 | 4.58× | 6,304 | 1,648 |
| TheoryLookupBenchmark.first | impl=immutable, query=joint-args, size=10000 | 115,978.961 ± 2,913.891 | 485,880.614 ± 157,971.419 | 4.19× | 12,976 | 3,136 |
| TheoryLookupBenchmark.first | impl=immutable, query=joint-args, size=100000 | ≈ 4.17e+04 (estimated) | 541,519.510 ± 212,585.182 | ≈ 13× | — | 2,704 |
| TheoryLookupBenchmark.first | impl=immutable, query=last-bound, size=10 | 267,384.587 ± 247,371.019 | 2,175,294.421 ± 493,562.328 | 8.14× | 10,528 | 1,584 |
| TheoryLookupBenchmark.first | impl=immutable, query=last-bound, size=1000 | 184.506 ± 55.253 | 1,968,651.378 ± 544,364.338 | 1.07e+04× | 5,604,997 | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=last-bound, size=10000 | 1.832 ± 0.611 | 1,881,408.016 ± 202,441.026 | 1.03e+06× | 596,064,309 | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=last-bound, size=100000 | ≈ 0.0182 (estimated) | 1,849,429.794 ± 915,770.263 | ≈ 1.02e+08× | — | 1,632 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg0, size=10 | 713,857.666 ± 299,136.857 | 1,820,629.091 ± 666,458.630 | 2.55× | 6,776 | 1,336 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg0, size=1000 | 589,037.220 ± 168,738.279 | 1,967,381.409 ± 44,508.001 | 3.34× | 6,928 | 1,352 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg0, size=10000 | 607,545.519 ± 408,190.459 | 1,881,735.424 ± 510,212.369 | 3.1× | 6,216 | 1,336 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg0, size=100000 | ≈ 6.27e+05 (estimated) | 1,839,440.364 ± 321,492.533 | ≈ 2.94× | — | 1,384 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg1, size=10 | 304,163.122 ± 107,637.946 | 1,787,605.185 ± 100,753.658 | 5.88× | 11,584 | 1,760 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg1, size=1000 | 6,960.885 ± 475.421 | 1,683,853.738 ± 118,134.323 | 242× | 399,505 | 1,808 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg1, size=10000 | 635.205 ± 179.878 | 1,501,707.192 ± 428,458.202 | 2.36e+03× | 4,168,051 | 1,672 |
| TheoryLookupBenchmark.first | impl=immutable, query=nested-arg1, size=100000 | ≈ 58 (estimated) | 1,529,006.143 ± 697,288.826 | ≈ 2.64e+04× | — | 1,672 |
| TheoryLookupBenchmark.first | impl=immutable, query=no-match, size=10 | 4,530,606.295 ± 1,651,998.376 | 33,681,472.646 ± 8,572,440.319 | 7.43× | 1,552 | 120 |
| TheoryLookupBenchmark.first | impl=immutable, query=no-match, size=1000 | 4,526,232.838 ± 1,357,004.139 | 26,633,373.866 ± 7,628,304.295 | 5.88× | 1,520 | 168 |
| TheoryLookupBenchmark.first | impl=immutable, query=no-match, size=10000 | 4,759,277.281 ± 585,983.829 | 20,968,366.153 ± 894,944.883 | 4.41× | 1,408 | 168 |
| TheoryLookupBenchmark.first | impl=immutable, query=no-match, size=100000 | ≈ 5e+06 (estimated) | 22,316,018.473 ± 722,883.378 | ≈ 4.46× | — | 168 |
| TheoryLookupBenchmark.first | impl=immutable, query=zero-arity, size=10 | 5,270,372.794 ± 562,415.911 | 4,902,983.547 ± 1,832,603.015 | 0.93× | 1,224 | 1,024 |
| TheoryLookupBenchmark.first | impl=immutable, query=zero-arity, size=1000 | 5,179,816.771 ± 632,110.581 | 4,773,436.071 ± 128,580.084 | 0.922× | 1,224 | 1,080 |
| TheoryLookupBenchmark.first | impl=immutable, query=zero-arity, size=10000 | 5,004,466.750 ± 1,513,594.894 | 5,244,895.089 ± 329,600.253 | 1.05× | 1,288 | 1,072 |
| TheoryLookupBenchmark.first | impl=immutable, query=zero-arity, size=100000 | ≈ 4.84e+06 (estimated) | 4,502,325.018 ± 1,603,701.666 | ≈ 0.931× | — | 1,080 |
| TheoryLookupBenchmark.first | impl=mutable, query=all-vars, size=10 | 574,117.389 ± 159,454.029 | 1,840,755.145 ± 525,501.308 | 3.21× | 6,440 | 1,856 |
| TheoryLookupBenchmark.first | impl=mutable, query=all-vars, size=1000 | 16,660.179 ± 1,581.584 | 1,880,411.637 ± 510,671.920 | 113× | 248,660 | 1,904 |
| TheoryLookupBenchmark.first | impl=mutable, query=all-vars, size=10000 | 1,666.059 ± 1,043.027 | 1,813,903.762 ± 164,860.232 | 1.09e+03× | 2,500,950 | 1,904 |
| TheoryLookupBenchmark.first | impl=mutable, query=all-vars, size=100000 | ≈ 167 (estimated) | 1,884,393.482 ± 110,272.419 | ≈ 1.13e+04× | — | 1,904 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg0-bound, size=10 | 1,179,730.615 ± 320,849.739 | 2,044,721.325 ± 536,920.836 | 1.73× | 3,424 | 1,680 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg0-bound, size=1000 | 1,160,018.398 ± 395,465.278 | 2,023,075.047 ± 597,266.173 | 1.74× | 3,424 | 1,728 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg0-bound, size=10000 | 1,042,316.165 ± 550,115.196 | 1,741,812.705 ± 728,402.984 | 1.67× | 3,424 | 1,792 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg0-bound, size=100000 | ≈ 9.37e+05 (estimated) | 1,932,672.644 ± 710,106.517 | ≈ 2.06× | — | 1,728 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg1-bound, size=10 | 250,832.465 ± 15,495.515 | 2,072,903.557 ± 558,218.317 | 8.26× | 11,344 | 1,680 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg1-bound, size=1000 | 6,086.683 ± 2,092.013 | 2,044,854.924 ± 37,811.358 | 336× | 350,465 | 1,728 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg1-bound, size=10000 | 574.269 ± 97.038 | 1,848,454.270 ± 453,908.695 | 3.22e+03× | 5,530,348 | 1,792 |
| TheoryLookupBenchmark.first | impl=mutable, query=arg1-bound, size=100000 | ≈ 54.2 (estimated) | 1,784,092.792 ± 756,983.377 | ≈ 3.29e+04× | — | 1,792 |
| TheoryLookupBenchmark.first | impl=mutable, query=joint-args, size=10 | 1,320,911.639 ± 172,568.268 | 1,605,093.913 ± 56,163.201 | 1.22× | 3,184 | 1,616 |
| TheoryLookupBenchmark.first | impl=mutable, query=joint-args, size=1000 | 292,385.947 ± 183,100.563 | 1,334,321.857 ± 382,430.683 | 4.56× | 6,280 | 1,816 |
| TheoryLookupBenchmark.first | impl=mutable, query=joint-args, size=10000 | 124,089.068 ± 43,649.713 | 481,307.838 ± 162,023.517 | 3.88× | 13,112 | 3,232 |
| TheoryLookupBenchmark.first | impl=mutable, query=joint-args, size=100000 | ≈ 5.27e+04 (estimated) | 498,780.378 ± 254,458.018 | ≈ 9.47× | — | 2,872 |
| TheoryLookupBenchmark.first | impl=mutable, query=last-bound, size=10 | 274,471.977 ± 60,179.603 | 2,089,287.870 ± 43,073.333 | 7.61× | 10,528 | 1,680 |
| TheoryLookupBenchmark.first | impl=mutable, query=last-bound, size=1000 | 188.098 ± 55.005 | 2,019,473.006 ± 384,288.103 | 1.07e+04× | 5,604,998 | 1,728 |
| TheoryLookupBenchmark.first | impl=mutable, query=last-bound, size=10000 | 1.839 ± 0.581 | 1,653,174.999 ± 272,640.124 | 8.99e+05× | 596,064,315 | 1,792 |
| TheoryLookupBenchmark.first | impl=mutable, query=last-bound, size=100000 | ≈ 0.018 (estimated) | 1,625,271.311 ± 363,197.134 | ≈ 9.04e+07× | — | 1,792 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg0, size=10 | 712,241.407 ± 139,170.182 | 1,863,398.916 ± 416,459.662 | 2.62× | 6,776 | 1,432 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg0, size=1000 | 628,161.963 ± 240,010.358 | 1,892,711.761 ± 424,642.760 | 3.01× | 6,680 | 1,480 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg0, size=10000 | 600,699.013 ± 202,079.691 | 1,895,903.267 ± 493,834.560 | 3.16× | 6,808 | 1,448 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg0, size=100000 | ≈ 5.74e+05 (estimated) | 1,872,423.814 ± 1,250,993.907 | ≈ 3.26× | — | 1,448 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg1, size=10 | 303,028.356 ± 3,708.015 | 1,791,507.202 ± 601,441.178 | 5.91× | 11,968 | 1,856 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg1, size=1000 | 7,100.855 ± 2,439.707 | 1,743,750.419 ± 35,842.932 | 246× | 399,481 | 1,904 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg1, size=10000 | 707.827 ± 215.959 | 1,471,941.325 ± 308,681.223 | 2.08e+03× | 3,327,529 | 1,832 |
| TheoryLookupBenchmark.first | impl=mutable, query=nested-arg1, size=100000 | ≈ 70.6 (estimated) | 1,568,153.927 ± 110,325.678 | ≈ 2.22e+04× | — | 1,768 |
| TheoryLookupBenchmark.first | impl=mutable, query=no-match, size=10 | 4,567,796.268 ± 1,410,484.555 | 46,985,804.955 ± 15,579,110.539 | 10.3× | 1,552 | 64 |
| TheoryLookupBenchmark.first | impl=mutable, query=no-match, size=1000 | 4,363,337.028 ± 110,422.303 | 31,131,280.090 ± 35,750,934.633 | 7.13× | 1,520 | 112 |
| TheoryLookupBenchmark.first | impl=mutable, query=no-match, size=10000 | 4,306,671.150 ± 1,980,872.508 | 29,427,787.947 ± 1,321,133.002 | 6.83× | 1,408 | 112 |
| TheoryLookupBenchmark.first | impl=mutable, query=no-match, size=100000 | ≈ 4.25e+06 (estimated) | 31,550,500.947 ± 5,852,499.817 | ≈ 7.42× | — | 112 |
| TheoryLookupBenchmark.first | impl=mutable, query=zero-arity, size=10 | 4,979,531.276 ± 1,396,897.061 | 4,764,201.481 ± 1,167,950.188 | 0.957× | 1,312 | 1,056 |
| TheoryLookupBenchmark.first | impl=mutable, query=zero-arity, size=1000 | 5,070,392.148 ± 864,151.681 | 4,589,756.903 ± 1,164,496.906 | 0.905× | 1,288 | 1,128 |
| TheoryLookupBenchmark.first | impl=mutable, query=zero-arity, size=10000 | 4,961,496.018 ± 2,190,328.504 | 4,578,346.014 ± 675,417.054 | 0.923× | 1,312 | 1,128 |
| TheoryLookupBenchmark.first | impl=mutable, query=zero-arity, size=100000 | ≈ 4.85e+06 (estimated) | 4,385,391.790 ± 1,437,278.177 | ≈ 0.903× | — | 1,128 |
| TheoryMutationBenchmark.update | impl=immutable, op=abolish, size=10 | 16,414.220 ± 7,855.003 | 520,100.802 ± 166,447.458 | 31.7× | 245,832 | 10,192 |
| TheoryMutationBenchmark.update | impl=immutable, op=abolish, size=1000 | 44.448 ± 11.901 | 4,785.912 ± 338.085 | 108× | 37,226,697 | 966,265 |
| TheoryMutationBenchmark.update | impl=immutable, op=abolish, size=10000 | 0.571 ± 0.161 | 449.884 ± 6.711 | 788× | 4,211,920,717 | 9,368,407 |
| TheoryMutationBenchmark.update | impl=immutable, op=abolish, size=100000 | ≈ 0.00733 (estimated) | 37.432 ± 12.924 | ≈ 5.11e+03× | — | 93,280,536 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertA, size=10 | 47,713.967 ± 12,865.710 | 4,182,507.099 ± 831,495.582 | 87.7× | 90,008 | 1,472 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertA, size=1000 | 572.023 ± 142.542 | 143,201.587 ± 5,185.187 | 250× | 8,296,315 | 49,040 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertA, size=10000 | 52.240 ± 27.829 | 16,601.310 ± 1,993.048 | 318× | 80,829,860 | 481,040 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertA, size=100000 | ≈ 4.77 (estimated) | 246.708 ± 83.926 | ≈ 51.7× | — | 4,801,252 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertZ, size=10 | 48,875.973 ± 5,923.654 | 4,141,855.680 ± 752,598.691 | 84.7× | 89,984 | 1,472 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertZ, size=1000 | 553.816 ± 155.637 | 143,373.173 ± 45,167.609 | 259× | 8,296,292 | 49,040 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertZ, size=10000 | 53.546 ± 13.478 | 16,780.959 ± 1,188.235 | 313× | 81,149,848 | 481,040 |
| TheoryMutationBenchmark.update | impl=immutable, op=assertZ, size=100000 | ≈ 5.18 (estimated) | 248.274 ± 226.970 | ≈ 48× | — | 4,801,252 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-first, size=10 | 15,317.189 ± 1,647.544 | 1,266,268.052 ± 751,485.669 | 82.7× | 247,554 | 2,416 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-first, size=1000 | 44.592 ± 15.851 | 67,511.687 ± 21,205.461 | 1.51e+03× | 36,342,081 | 45,096 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-first, size=10000 | 0.611 ± 0.228 | 6,944.297 ± 2,075.302 | 1.14e+04× | 1,801,025,011 | 451,201 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-first, size=100000 | ≈ 0.00838 (estimated) | 224.797 ± 58.849 | ≈ 2.68e+04× | — | 4,083,551 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-last, size=10 | 15,704.228 ± 6,133.918 | 1,253,501.141 ± 462,462.367 | 79.8× | 267,421 | 2,416 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-last, size=1000 | 49.026 ± 2.321 | 64,681.093 ± 27,935.563 | 1.32e+03× | 35,918,042 | 45,096 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-last, size=10000 | 0.604 ± 0.269 | 6,668.466 ± 3,470.075 | 1.1e+04× | 1,797,735,571 | 451,201 |
| TheoryMutationBenchmark.update | impl=immutable, op=retract-last, size=100000 | ≈ 0.00744 (estimated) | 217.945 ± 40.063 | ≈ 2.93e+04× | — | 4,083,552 |
| TheoryMutationBenchmark.update | impl=immutable, op=retractAll, size=10 | 14,945.483 ± 4,258.969 | 1,210,935.270 ± 341,191.390 | 81× | 245,976 | 2,584 |
| TheoryMutationBenchmark.update | impl=immutable, op=retractAll, size=1000 | 43.775 ± 10.720 | 33,360.573 ± 2,713.394 | 762× | 37,027,709 | 69,912 |
| TheoryMutationBenchmark.update | impl=immutable, op=retractAll, size=10000 | 0.603 ± 0.245 | 3,515.607 ± 1,384.708 | 5.83e+03× | 1,805,008,949 | 716,146 |
| TheoryMutationBenchmark.update | impl=immutable, op=retractAll, size=100000 | ≈ 0.00832 (estimated) | 153.621 ± 86.539 | ≈ 1.85e+04× | — | 6,747,477 |
| TheoryMutationBenchmark.update | impl=mutable, op=abolish, size=10 | 127,113.692 ± 53,301.501 | 182,864.433 ± 8,449.660 | 1.44× | 30,668 | 21,827 |
| TheoryMutationBenchmark.update | impl=mutable, op=abolish, size=1000 | 1,599.714 ± 552.103 | 1,951.324 ± 208.412 | 1.22× | 2,975,538 | 2,154,927 |
| TheoryMutationBenchmark.update | impl=mutable, op=abolish, size=10000 | 104.364 ± 33.679 | 173.460 ± 60.850 | 1.66× | 29,789,611 | 20,932,802 |
| TheoryMutationBenchmark.update | impl=mutable, op=abolish, size=100000 | ≈ 6.81 (estimated) | 14.234 ± 4.004 | ≈ 2.09× | — | 208,953,745 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertA, size=10 | 1,780,325.309 ± 161,649.991 | 1,795,793.297 ± 40,162.136 | 1.01× | 3,224 | 1,210 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertA, size=1000 | 1,777,130.840 ± 607,761.452 | 1,504,162.301 ± 922,405.611 | 0.846× | 3,232 | 1,401 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertA, size=10000 | 1,814,209.116 ± 488,547.225 | 1,477,305.319 ± 413,329.095 | 0.814× | 2,888 | 1,408 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertA, size=100000 | ≈ 1.85e+06 (estimated) | 1,481,439.162 ± 697,523.396 | ≈ 0.8× | — | 1,446 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertZ, size=10 | 1,849,543.363 ± 271,545.314 | 1,803,642.210 ± 158,359.530 | 0.975× | 3,208 | 1,236 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertZ, size=1000 | 1,672,285.348 ± 388,571.306 | 1,513,572.417 ± 631,305.918 | 0.905× | 3,224 | 1,425 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertZ, size=10000 | 1,756,313.483 ± 508,733.583 | 1,523,670.896 ± 382,016.556 | 0.868× | 3,224 | 1,460 |
| TheoryMutationBenchmark.update | impl=mutable, op=assertZ, size=100000 | ≈ 1.84e+06 (estimated) | 1,540,065.070 ± 604,778.748 | ≈ 0.835× | — | 1,456 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-first, size=10 | 989,565.688 ± 421,270.328 | 1,592,720.505 ± 298,463.921 | 1.61× | 4,112 | 1,782 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-first, size=1000 | 942,651.035 ± 157,993.704 | 1,453,496.941 ± 419,118.391 | 1.54× | 4,112 | 1,969 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-first, size=10000 | 994,224.654 ± 228,494.914 | 1,228,492.129 ± 313,868.443 | 1.24× | 4,112 | 2,144 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-first, size=100000 | ≈ 1.05e+06 (estimated) | 1,165,056.134 ± 871,388.540 | ≈ 1.11× | — | 2,183 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-last, size=10 | 998,769.836 ± 408,006.026 | 1,533,569.512 ± 398,030.014 | 1.54× | 4,112 | 1,791 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-last, size=1000 | 950,512.663 ± 359,795.961 | 1,517,353.790 ± 390,511.228 | 1.6× | 4,112 | 1,993 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-last, size=10000 | 989,372.174 ± 162,009.857 | 1,294,511.910 ± 479,315.429 | 1.31× | 4,056 | 2,156 |
| TheoryMutationBenchmark.update | impl=mutable, op=retract-last, size=100000 | ≈ 1.03e+06 (estimated) | 1,207,121.655 ± 384,335.778 | ≈ 1.17× | — | 2,194 |
| TheoryMutationBenchmark.update | impl=mutable, op=retractAll, size=10 | 266,056.988 ± 54,497.925 | 1,307,277.549 ± 435,767.884 | 4.91× | 13,184 | 2,095 |
| TheoryMutationBenchmark.update | impl=mutable, op=retractAll, size=1000 | 3,252.899 ± 1,420.949 | 71,522.045 ± 16,929.410 | 22× | 716,140 | 34,503 |
| TheoryMutationBenchmark.update | impl=mutable, op=retractAll, size=10000 | 301.567 ± 137.019 | 7,099.585 ± 279.216 | 23.5× | 7,225,895 | 331,762 |
| TheoryMutationBenchmark.update | impl=mutable, op=retractAll, size=100000 | ≈ 28 (estimated) | 667.864 ± 295.029 | ≈ 23.9× | — | 3,429,379 |
| TheorySolverBenchmark.solveAll | workload=determinism | 74.033 ± 28.152 | 144.929 ± 30.518 | 1.96× | 41,344,024 | 24,155,145 |
| TheorySolverBenchmark.solveAll | workload=dynamic-churn | 86.905 ± 43.561 | 142.647 ± 71.417 | 1.64× | 30,910,684 | 22,460,870 |
| TheorySolverBenchmark.solveAll | workload=dynamic-counter | 28.202 ± 32.536 | 93.913 ± 71.760 | 3.33× | 83,781,557 | 32,842,447 |
| TheorySolverBenchmark.solveAll | workload=dynamic-table-arg0 | 92.680 ± 25.812 | 161.210 ± 20.817 | 1.74× | 28,953,697 | 19,516,576 |
| TheorySolverBenchmark.solveAll | workload=fact-table-arg0 | 92.040 ± 8.084 | 160.575 ± 48.427 | 1.74× | 28,672,717 | 19,464,592 |
| TheorySolverBenchmark.solveAll | workload=fact-table-arg1 | 0.002 ± 0.001 | 160.837 ± 13.840 | 6.98e+04× | 415,327,903,752 | 19,869,036 |

## Longer zero-arity check

Size 1000; three 1-second warm-ups, five 1-second measurements, two forks per branch. Separate from the campaign aggregates. Errors are JMH 99.9% intervals.

| Operation | Implementation | Develop ops/s | Branch ops/s | Speedup |
|---|---|---:|---:|---:|
| all | immutable | 6,194 ± 876 | 6,052 ± 101 | 0.98× |
| all | mutable | 6,769 ± 79 | 5,917 ± 172 | 0.87× |
| first | immutable | 4,715,375 ± 79,366 | 4,646,022 ± 115,081 | 0.99× |
| first | mutable | 4,660,000 ± 113,012 | 4,507,524 ± 172,642 | 0.97× |
