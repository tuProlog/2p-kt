package it.unibo.tuprolog.utils.time

/**
 * A [Timeline] counting the [reads] of its clock, i.e. the time marks taken from its [source]: instrumentation for
 * tests and benchmarks, e.g. to assert how often a [Chronometer] samples the time.
 */
interface CountingTimeline : Timeline {
    /** How many time marks have been taken from [source] so far. */
    val reads: Long
}
