package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.solve.ResolutionCounters
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.solve.timing.timed

/**
 * Deterministic counts of the logical work each [Workload] takes on `:solve-prolog` (#1010), as a Markdown table:
 * [ResolutionCounters] (nested resolutions such as `findall/3`'s excluded), and the number of answers. The last two
 * columns time one cold run with [timed], so they only give an idea; use the JMH benchmarks for timings.
 *
 * Run with `./gradlew :examples:resolutionCountersReport`.
 */
object ResolutionCountersReport {
    @JvmStatic
    fun main(args: Array<String>) {
        println(
            "| Workload | Inferences | Alternatives | Choice points | Max open | Max depth | Answers | " +
                "First answer | Active |",
        )
        println("|---|---:|---:|---:|---:|---:|---:|---:|---:|")
        for (workload in Workload.entries) {
            val counters = ResolutionCounters()
            val solutions =
                PrologSolverFactory
                    .solverWithDefaultBuiltins(staticKb = workload.theory, flags = workload.flags)
                    .solve(workload.goal, TimeoutSetting.NONE.options.setCounters(counters))
                    .timed()
            solutions.forEachIndexed { i, solution -> if (i == 0) workload.checkSolution(solution) }
            val timing = solutions.timing
            val cells =
                listOf(
                    workload.id,
                    counters.inferences,
                    counters.alternatives,
                    counters.choicePoints,
                    counters.maxChoicePoints,
                    counters.maxDepth,
                    timing.answers,
                    timing.firstAnswer,
                    timing.active,
                )
            println(cells.joinToString(" | ", "| ", " |"))
        }
    }
}
