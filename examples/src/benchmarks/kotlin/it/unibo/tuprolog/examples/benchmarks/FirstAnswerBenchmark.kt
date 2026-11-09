package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolveOptions
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Average time to the first answer of each [Workload] with `:solve-prolog`, without a timeout (#1010): what laziness
 * saves when the remaining alternatives are never needed.
 */
@State(Scope.Benchmark)
class FirstAnswerBenchmark {
    @Param("backtracking", "failing-search", "first-of-many", "dynamic-churn")
    var workload: String = "backtracking"

    private lateinit var solver: Solver
    private lateinit var goal: Struct
    private val options: SolveOptions = TimeoutSetting.NONE.options

    @Setup
    fun setUp() {
        val workload = Workload.byId(workload)
        solver = PrologSolverFactory.solverWithDefaultBuiltins(staticKb = workload.theory, flags = workload.flags)
        goal = workload.goal
        workload.checkSolution(solver.solveOnce(goal))
    }

    @Benchmark
    fun solveFirst(): Solution = solver.solve(goal, options).use { it.first() }
}
