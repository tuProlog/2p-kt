package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/** Average time to solve each [Workload] to exhaustion with `:solve-prolog`, per [TimeoutSetting] (#1012). */
@State(Scope.Benchmark)
class PrologSolverBenchmark {
    @Param("tiny", "deep-recursion", "nrev", "backtracking", "failing-search", "nested-solving", "primitives")
    var workload: String = "tiny"

    @Param("none", "large")
    var timeout: String = "none"

    private lateinit var solver: Solver
    private lateinit var goal: Struct
    private lateinit var options: SolveOptions

    @Setup
    fun setUp() {
        solver = PrologSolverFactory.solverWithDefaultBuiltins(staticKb = BenchmarkTheory.theory)
        goal = Workload.byId(workload).goal
        options = TimeoutSetting.byId(timeout).options
    }

    @Benchmark
    fun solveAll(blackhole: Blackhole) {
        for (solution in solver.solve(goal, options)) {
            blackhole.consume(solution)
        }
    }
}
