package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solver
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/** Average time to solve each [TheorySolverWorkload] to exhaustion with `:solve-prolog` (#1019). */
@State(Scope.Benchmark)
class TheorySolverBenchmark {
    @Param(
        "fact-table-arg0",
        "fact-table-arg1",
        "dynamic-table-arg0",
        "dynamic-counter",
        "dynamic-churn",
        "determinism",
    )
    var workload: String = "fact-table-arg0"

    private lateinit var solver: Solver
    private lateinit var goal: Struct

    @Setup
    fun setUp() {
        solver = TheorySolverWorkload.solver()
        goal = TheorySolverWorkload.byId(workload).goal
    }

    @Benchmark
    fun solveAll(blackhole: Blackhole) {
        for (solution in solver.solve(goal)) {
            blackhole.consume(solution)
        }
    }
}
