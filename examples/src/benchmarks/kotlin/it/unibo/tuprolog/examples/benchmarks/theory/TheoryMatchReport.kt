package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.examples.benchmarks.BenchmarkTheory
import it.unibo.tuprolog.examples.benchmarks.Workload
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.unify.Unificator

/**
 * Deterministic counts behind `TheoryLookupBenchmark` (#1019): for each implementation and [TheoryQuery], how many
 * `match` calls the theory performs to find the first match (`first`) and every match (`all`), with
 * [TheoryWorkloads.standard] of size [SIZE]. Then, for each solver workload, how many `match` and `mgu` calls solving
 * it to exhaustion takes, i.e. how many unifications resolution performs. Prints Markdown tables.
 *
 * Run with `./gradlew :examples:theoryMatchReport`.
 */
object TheoryMatchReport {
    const val SIZE = 1000

    @JvmStatic
    fun main(args: Array<String>) {
        val impls = TheoryImpl.entries
        println("| Query | Matches | " + impls.joinToString(" | ") { it.id } + " |")
        println("|---|---|" + impls.joinToString("") { "---:|" })
        for (query in TheoryQuery.entries) {
            for (mode in listOf("first", "all")) {
                val counts =
                    impls.map { impl ->
                        val unificator = CountingUnificator()
                        val theory = impl.build(TheoryWorkloads.standard(SIZE), unificator)
                        val pattern = query.query(SIZE)
                        unificator.reset()
                        if (mode == "first") theory[pattern].firstOrNull() else theory[pattern].count()
                        unificator.matches
                    }
                println("| ${query.id} | $mode | " + counts.joinToString(" | ") + " |")
            }
        }
        println()
        println("| Solver workload | match | mgu | total |")
        println("|---|---:|---:|---:|")
        val workloads =
            TheorySolverWorkload.entries.map { w ->
                w.id to
                    { u: Unificator -> TheorySolverWorkload.solver(u) to w.goal }
            } +
                listOf(Workload.NREV, Workload.DEEP_RECURSION, Workload.BACKTRACKING).map { w ->
                    w.id to { u: Unificator ->
                        PrologSolverFactory.solverWithDefaultBuiltins(
                            unificator = u,
                            staticKb = BenchmarkTheory.theory.setUnificator(u),
                        ) to w.goal
                    }
                }
        for ((id, setUp) in workloads) {
            val unificator = CountingUnificator()
            val (solver, goal) = setUp(unificator)
            unificator.reset()
            solver.solve(goal).forEach { _ -> }
            val total = unificator.matches + unificator.mgus
            println("| $id | ${unificator.matches} | ${unificator.mgus} | $total |")
        }
    }
}
