package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.UnificationStep

/** The customisation points of an [it.unibo.tuprolog.unify.AbstractWorklistUnificator], as seen by its engine. */
internal interface UnificationHooks {
    fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean

    fun handleComparison(step: UnificationStep)
}
