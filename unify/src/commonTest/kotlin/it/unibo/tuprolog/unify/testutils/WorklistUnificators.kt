package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.Unificator

/** The [AbstractWorklistUnificator] strategies under test, independently of what [Unificator]'s factories return. */
internal object WorklistUnificators {
    fun strict(context: Substitution = Substitution.empty()): Unificator =
        object : AbstractWorklistUnificator(context) {}

    fun naive(context: Substitution = Substitution.empty()): Unificator =
        object : AbstractWorklistUnificator(context) {
            override fun checkTermsEquality(
                first: Term,
                second: Term,
            ): Boolean = NumericEquality.byValue(first, second)
        }
}
