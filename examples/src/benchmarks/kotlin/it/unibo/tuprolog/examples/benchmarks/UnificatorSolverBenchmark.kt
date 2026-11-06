package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/** Throughput of `:solve-prolog` solving each [Workload] to exhaustion, per unification engine (#1020). */
@State(Scope.Benchmark)
class UnificatorSolverBenchmark {
    @Param("tiny", "deep-recursion", "nrev", "backtracking", "failing-search", "nested-solving", "primitives")
    var workload: String = "tiny"

    @Param("equational", "worklist")
    var engine: String = "worklist"

    private lateinit var solver: Solver
    private lateinit var goal: Struct

    @Setup
    fun setUp() {
        val unificator = UnificatorSetting.byId(engine).unificator
        val workload = Workload.byId(workload)
        solver =
            PrologSolverFactory.solverWithDefaultBuiltins(
                unificator = unificator,
                staticKb = workload.theory,
                flags = workload.flags,
            )
        goal = workload.goal
        require(solver.solveOnce(goal).isYes) { "Workload ${workload.id} has no solution" }
    }

    @Benchmark
    fun solveAll(blackhole: Blackhole) {
        for (solution in solver.solve(goal)) {
            blackhole.consume(solution)
        }
    }
}
