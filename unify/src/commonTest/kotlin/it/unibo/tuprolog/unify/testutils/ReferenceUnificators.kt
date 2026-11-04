@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator
import it.unibo.tuprolog.unify.Unificator

/** The equation-based [AbstractUnificator] strategies: the reference the worklist engine is compared against. */
internal object ReferenceUnificators {
    fun strict(context: Substitution = Substitution.empty()): Unificator =
        object : AbstractUnificator(context) {
            override fun checkTermsEquality(
                first: Term,
                second: Term,
            ): Boolean = first == second
        }

    fun naive(context: Substitution = Substitution.empty()): Unificator =
        object : AbstractUnificator(context) {
            override fun checkTermsEquality(
                first: Term,
                second: Term,
            ): Boolean = NumericEquality.byValue(first, second)
        }
}
