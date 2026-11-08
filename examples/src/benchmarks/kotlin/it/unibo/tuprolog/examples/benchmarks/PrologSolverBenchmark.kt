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

/** Average time to solve each [Workload] to exhaustion with `:solve-prolog`, per [TimeoutSetting] and [ChronometerSetting] (#1012). */
@State(Scope.Benchmark)
class PrologSolverBenchmark {
    @Param(
        "tiny",
        "deep-recursion",
        "nrev",
        "nrev-100",
        "bind-vars",
        "strips-list",
        "strips-tuple",
        "strips-block",
        "backtracking",
        "failing-search",
        "nested-solving",
        "primitives",
        "cut-catch",
        "dynamic-churn",
        "first-of-many",
        "arithmetic",
    )
    var workload: String = "tiny"

    @Param("none", "large")
    var timeout: String = "none"

    @Param("direct", "adaptive")
    var chronometer: String = "direct"

    private lateinit var solver: Solver
    private lateinit var goal: Struct
    private lateinit var options: SolveOptions

    @Setup
    fun setUp() {
        val workload = Workload.byId(workload)
        solver = PrologSolverFactory.solverWithDefaultBuiltins(staticKb = workload.theory, flags = workload.flags)
        goal = workload.goal
        workload.checkSolution(solver.solveOnce(goal))
        options =
            TimeoutSetting.byId(timeout).options.setChronometerFactory(ChronometerSetting.byId(chronometer).factory)
    }

    @Benchmark
    fun solveAll(blackhole: Blackhole) {
        for (solution in solver.solve(goal, options)) {
            blackhole.consume(solution)
        }
    }
}
