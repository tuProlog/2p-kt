package it.unibo.tuprolog.examples.benchmarks

/** Counts the resolution steps (leaps) and forced checks seen by a [CountingChronometer] and its child scopes. */
class LeapCounter {
    /** How many times `leap` was called. */
    var leaps: Long = 0
        internal set

    /** How many times `checkNow` was called. */
    var checks: Long = 0
        internal set
}
