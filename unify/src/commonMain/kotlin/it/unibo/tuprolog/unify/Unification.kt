package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term

interface Unification {

    /** The context (in terms of already present bindings) in which the unification is performed */
    val context: Substitution

    /** Calculates the Most General Unifier of given [Term]s, optionally enabling occur-check */
    fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean = true): Substitution

    /** Tells whether two [Term]s match each other, that is there's a Most General Unifier for them */
    fun match(term1: Term, term2: Term, occurCheckEnabled: Boolean = true): Boolean =
            mgu(term1, term2, occurCheckEnabled) !== Substitution.failed()

    /** Unifies two [Term]s if possible */
    fun unify(term1: Term, term2: Term, occurCheckEnabled: Boolean = true): Term? {
        val substitution = mgu(term1, term2, occurCheckEnabled)
        return if (substitution.isFailed) null else term1[substitution]
    }

    companion object {

        val default by lazy { naive() }

        infix fun Term.mguWith(other: Term): Substitution? {
            return default.mgu(this, other)
        }

        infix fun Term.unifyWith(other: Term): Term? {
            return default.unify(this, other)
        }

        infix fun Term.matches(other: Term): Boolean {
            return default.match(this, other)
        }

        fun naive(context: Substitution = Substitution.empty()): Unification {
            return object : AbstractUnificationStrategy(context) {
                override fun checkTermsEquality(first: Term, second: Term): Boolean = first == second
            }
        }

        fun strict(context: Substitution = Substitution.empty()): Unification {
            return object : AbstractUnificationStrategy(context) {
                override fun checkTermsEquality(first: Term, second: Term): Boolean = first strictlyEquals second
            }
        }
    }
}