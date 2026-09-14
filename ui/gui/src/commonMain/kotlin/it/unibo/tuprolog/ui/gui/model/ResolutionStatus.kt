package it.unibo.tuprolog.ui.gui.model

/** The lifecycle of one page's resolution. */
enum class ResolutionStatus {
    /** No resolution has run yet, or the last one's outcome was cleared. */
    IDLE,

    /** Actively computing towards the next solution. */
    RUNNING,

    /** Paused after yielding a solution, waiting for a "next"/"stop" action. */
    AWAITING_CONTINUATION,

    /** Ran to exhaustion with no further solutions. */
    COMPLETED,

    /** Ended with an unrecoverable error. */
    FAILED,

    /** Was cancelled before completing. */
    CANCELLED,
}
