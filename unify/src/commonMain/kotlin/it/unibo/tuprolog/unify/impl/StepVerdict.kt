package it.unibo.tuprolog.unify.impl

/** What a [it.unibo.tuprolog.unify.UnificationStep] callback decided about the current pair. */
internal enum class StepVerdict {
    PROCEED,
    REPLACED,
    ACCEPTED,
    REJECTED,
}
