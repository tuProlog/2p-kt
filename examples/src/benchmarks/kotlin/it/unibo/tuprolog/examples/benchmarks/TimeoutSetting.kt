package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.solve.SolveOptions
import kotlin.time.Duration.Companion.hours

/** The timeout configurations the benchmarks compare, by JMH `timeout` parameter value. */
enum class TimeoutSetting(
    val id: String,
    val options: SolveOptions,
) {
    /** No timeout at all. */
    NONE("none", SolveOptions.allLazily()),

    /** A timeout far beyond the workload's duration: checking it costs, but it never fires. */
    LARGE("large", SolveOptions.allLazilyWithTimeout(1.hours)),
    ;

    companion object {
        /** The [TimeoutSetting] whose [id] is [id]. */
        fun byId(id: String): TimeoutSetting = entries.first { it.id == id }
    }
}
