package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface Unificator {

    /** The context (in terms of already present bindings) in which the unification is performed */
    val context: Substitution

    /** Calculates the Most General Unifier of given [Term]s, optionally enabling occur-check */
    fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean = true): Substitution

    /** Calculates the Most General Unifier of given [Term]s, enabling occur-check */
    fun mgu(term1: Term, term2: Term): Substitution =
        mgu(term1, term2, true)

    /** Tells whether two [Term]s match each other, that is there's a Most General Unifier for them */
    fun match(term1: Term, term2: Term, occurCheckEnabled: Boolean): Boolean =
        mgu(term1, term2, occurCheckEnabled) !== Substitution.failed()

    /** Tells whether two [Term]s match each other, that is there's a Most General Unifier for them.
     * It performs unification with occur check*/
    fun match(term1: Term, term2: Term): Boolean =
        match(term1, term2, true)

    /** Unifies two [Term]s if possible */
    fun unify(term1: Term, term2: Term, occurCheckEnabled: Boolean): Term? {
        val substitution = mgu(term1, term2, occurCheckEnabled)
        return if (substitution.isFailed) null else term1[substitution]
    }

    /** Unifies two [Term]s if possible, with occurs check */
    fun unify(term1: Term, term2: Term): Term? =
        unify(term1, term2, true)

    companion object {

        /** The default unification strategy that uses plain equals to determine [Term]s identity */
        @JvmStatic
        val default by lazy { strict() }

        /** Computes the Most General Unifier, using [default] unification strategy */
        @JvmStatic
        infix fun Term.mguWith(other: Term): Substitution = default.mgu(this, other)

        /** Computes whether the two terms match, using [default] unification strategy */
        @JvmStatic
        infix fun Term.matches(other: Term): Boolean = default.match(this, other)

        /** Computes the unified term, using [default] unification strategy */
        @JvmStatic
        infix fun Term.unifyWith(other: Term): Term? = default.unify(this, other)

        /** Creates naive unification strategy, with the given [context], that checks [Term]s identity through their [Term.equals]
         * methods, except in the case of numbers which are compared by value */
        @JvmStatic
        fun naive(context: Substitution): Unificator =
            object : AbstractUnificationStrategy(context) {
                override fun checkTermsEquality(first: Term, second: Term) =
                    when {
                        first is Integer && second is Integer -> first.value.compareTo(second.value) == 0
                        first is Numeric && second is Numeric -> first.decimalValue.compareTo(second.decimalValue) == 0
                        else -> first == second
                    }
            }

        /** Creates naive, empty unification strategy, that checks [Term]s identity through their [Term.equals]
         * methods, except in the case of numbers which are compared by value */
        @JvmStatic
        fun naive(): Unificator =
            naive(Substitution.empty())

        /** Creates naive unification strategy, with the given [context], that checks [Term]s identity through their [Term.equals]
         * methods */
        @JvmStatic
        fun strict(context: Substitution): Unificator =
            object : AbstractUnificationStrategy(context) {
                override fun checkTermsEquality(first: Term, second: Term) = first == second
            }

        /** Creates naive unification strategy, that checks [Term]s identity through their [Term.equals]
         * methods */
        @JvmStatic
        fun strict(): Unificator =
            strict(Substitution.empty())
    }
}