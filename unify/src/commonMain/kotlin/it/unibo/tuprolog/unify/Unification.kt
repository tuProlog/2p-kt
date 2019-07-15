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

        /** The default unification strategy that uses plain equals to determine [Term]s identity */
        val default by lazy { naive() }

        /** Computes the Most General Unifier, using [default] unification strategy */
        infix fun Term.mguWith(other: Term): Substitution? = default.mgu(this, other)

        /** Computes whether the two terms match, using [default] unification strategy */
        infix fun Term.matches(other: Term): Boolean = default.match(this, other)

        /** Computes the unified term, using [default] unification strategy */
        infix fun Term.unifyWith(other: Term): Term? = default.unify(this, other)

        /** Creates naive unification strategy, with given context, that checks [Term]s identity with it's [equals] */
        fun naive(context: Substitution = Substitution.empty()): Unification =
                object : AbstractUnificationStrategy(context) {
                    override fun checkTermsEquality(first: Term, second: Term) = first == second
                }

        /** Creates a strict unification strategy, with given context, that checks [Term]s identity with [Term.strictlyEquals] */
        fun strict(context: Substitution = Substitution.empty()): Unification =
                object : AbstractUnificationStrategy(context) {
                    override fun checkTermsEquality(first: Term, second: Term) = first strictlyEquals second
                }
    }
}