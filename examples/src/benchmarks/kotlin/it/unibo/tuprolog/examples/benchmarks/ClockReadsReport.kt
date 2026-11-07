package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.utils.time.AdaptiveSamplingPolicy
import it.unibo.tuprolog.utils.time.ChronometerFactory
import it.unibo.tuprolog.utils.time.Timeline
import kotlin.time.Duration

/**
 * Deterministic counts behind the timing benchmarks (#1012): for each [Workload] solved by `:solve-prolog` with a
 * large timeout, how many resolution steps (leaps) and forced checks the chronometers see (nested resolutions
 * included), and how many times the system clock is read by the direct and by the adaptive chronometer. Prints a
 * Markdown table. Leaps and forced checks don't depend on the chronometer; adaptive reads depend on the machine's
 * speed, as they are planned from the measured duration of a leap.
 *
 * Run with `./gradlew :examples:clockReadsReport`.
 */
object ClockReadsReport {
    private class Counts(
        val leaps: Long,
        val checks: Long,
        val reads: Long,
    )

    private fun count(
        workload: Workload,
        timeout: Duration,
        factoryOn: (Timeline) -> ChronometerFactory,
    ): Counts {
        val timeline = Timeline.counting(Timeline.system)
        val counter = LeapCounter()
        val factory = ChronometerFactory { CountingChronometer(factoryOn(timeline).create(it), counter) }
        val options =
            TimeoutSetting.LARGE.options
                .setTimeout(timeout)
                .setChronometerFactory(factory)
        PrologSolverFactory
            .solverWithDefaultBuiltins(staticKb = workload.theory, flags = workload.flags)
            .solve(workload.goal, options)
            .toList()
        return Counts(counter.leaps, counter.checks, timeline.reads)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val timeout = TimeoutSetting.LARGE.options.timeout
        println(
            "| Workload | Leaps | Forced checks | Reads (direct) | Reads (adaptive) | Reads (adaptive, no timeout) |",
        )
        println("|---|---:|---:|---:|---:|---:|")
        for (workload in Workload.entries) {
            val direct = count(workload, timeout) { ChronometerFactory.direct(it) }
            val adaptive = count(workload, timeout) { ChronometerFactory.adaptive(AdaptiveSamplingPolicy.DEFAULT, it) }
            val unlimited = count(workload, Duration.INFINITE) { ChronometerFactory.adaptive(timeline = it) }
            println(
                "| ${workload.id} | ${direct.leaps} | ${direct.checks} | ${direct.reads} | ${adaptive.reads} | " +
                    "${unlimited.reads} |",
            )
        }
    }
}
