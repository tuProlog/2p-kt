package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Unificator {

    /** The context (in terms of already present bindings) in which the unification is performed */
    @JsName("context")
    val context: Substitution

    /** Calculates the Most General Unifier of given [Term]s, optionally enabling occur-check */
    @JsName("mguWithOccurCheck")
    fun mgu(term1: Term, term2: Term, occurCheckEnabled: Boolean = true): Substitution

    /** Calculates the Most General Unifier of given [Term]s, enabling occur-check */
    @JsName("mgu")
    fun mgu(term1: Term, term2: Term): Substitution =
        mgu(term1, term2, true)

    /** Tells whether two [Term]s match each other, that is there's a Most General Unifier for them */
    @JsName("matchWithOccurCheck")
    fun match(term1: Term, term2: Term, occurCheckEnabled: Boolean): Boolean =
        mgu(term1, term2, occurCheckEnabled) !== Substitution.failed()

    /** Tells whether two [Term]s match each other, that is there's a Most General Unifier for them.
     * It performs unification with occur check*/
    @JsName("match")
    fun match(term1: Term, term2: Term): Boolean =
        match(term1, term2, true)

    /** Unifies two [Term]s if possible */
    @JsName("unifyWithOccurCheck")
    fun unify(term1: Term, term2: Term, occurCheckEnabled: Boolean): Term? {
        val substitution = mgu(term1, term2, occurCheckEnabled)
        return if (substitution.isFailed) null else term1[substitution]
    }

    /** Unifies two [Term]s if possible, with occurs check */
    @JsName("unify")
    fun unify(term1: Term, term2: Term): Term? =
        unify(term1, term2, true)

    companion object {

        /**
         * The default unification strategy that uses plain equals to determine [Term]s identity, and exploits an
         * LRU cache whose capacity is [DEFAULT_CACHE_CAPACITY]
         */
        @JvmStatic
        @JsName("default")
        val default by lazy { cached(strict()) }

        /** Computes the Most General Unifier, using [default] unification strategy */
        @JvmStatic
        @JsName("mguWith")
        infix fun Term.mguWith(other: Term): Substitution = default.mgu(this, other)

        /** Computes whether the two terms match, using [default] unification strategy */
        @JvmStatic
        @JsName("matches")
        infix fun Term.matches(other: Term): Boolean = default.match(this, other)

        /** Computes the unified term, using [default] unification strategy */
        @JvmStatic
        @JsName("unifyWith")
        infix fun Term.unifyWith(other: Term): Term? = default.unify(this, other)

        /** Creates naive unification strategy, with the given [context], that checks [Term]s identity through their [Term.equals]
         * methods, except in the case of numbers which are compared by value */
        @JvmStatic
        @JsName("naiveWithContext")
        fun naive(context: Substitution): Unificator =
            object : AbstractUnificator(context) {
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
        @JsName("naive")
        fun naive(): Unificator =
            naive(Substitution.empty())

        /** Creates naive unification strategy, with the given [context], that checks [Term]s identity through their [Term.equals]
         * methods */
        @JvmStatic
        @JsName("strictWithContext")
        fun strict(context: Substitution): Unificator =
            object : AbstractUnificator(context) {
                override fun checkTermsEquality(first: Term, second: Term) = first == second
            }

        /** Creates naive unification strategy, that checks [Term]s identity through their [Term.equals]
         * methods */
        @JvmStatic
        @JsName("strict")
        fun strict(): Unificator =
            strict(Substitution.empty())

        /**
         * Makes another unification strategy cached, by letting memorising the most recent terms unified through it
         * @param other is the [Unificator] to be made cached
         * @param capacity is the maximum amount of items the cache may store
         * @return a decorated [Unificator]
         */
        @JvmStatic
        @JsName("cached")
        fun cached(other: Unificator, capacity: Int = DEFAULT_CACHE_CAPACITY): Unificator =
            if (other is CachedUnificator) {
                CachedUnificator(other.decorated, capacity)
            } else {
                CachedUnificator(other, capacity)
            }

        const val DEFAULT_CACHE_CAPACITY = 32
    }
}