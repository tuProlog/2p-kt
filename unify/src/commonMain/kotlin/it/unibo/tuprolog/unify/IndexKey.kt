package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A coarse abstraction of a [Term] that clause indexes use to discard clauses which cannot unify with a
 * query without attempting unification. A [Unificator] provides keys through [Unificator.indexKey], under a contract
 * analogous to the one between `hashCode` and `equals`: **if two terms unify, either their keys are equal, or one of
 * them is not [isExact]**. Keys may therefore merge terms that do not unify (indexing becomes less selective, never
 * wrong), but must never separate terms that do.
 *
 * Keys ignore tags: a unificator taking tags into account only to *reject* more pairs keeps the default keys.
 */
sealed class IndexKey {
    /** Whether this key identifies a precise principal symbol, i.e., it can be used to look up an index bucket. */
    @JsName("isExact")
    abstract val isExact: Boolean

    /** An atom, by its value. */
    data class OfAtom(
        val value: String,
    ) : IndexKey() {
        override val isExact: Boolean get() = true
    }

    /** An integer, by its value. */
    data class OfInteger(
        val value: BigInteger,
    ) : IndexKey() {
        override val isExact: Boolean get() = true
    }

    /** A real number (or any number, for unificators comparing numbers by value), by its approximate value. */
    data class OfNumber(
        val value: Double,
    ) : IndexKey() {
        override val isExact: Boolean get() = true
    }

    /** A compound term, by its functor and arity. */
    data class OfStruct(
        val functor: String,
        val arity: Int,
    ) : IndexKey() {
        override val isExact: Boolean get() = true
    }

    /**
     * Any compound term with the given [arity], whatever its functor: reserved for future patterns, i.e. structures
     * whose functor is a variable. Indexes treat it like [Wildcard].
     */
    data class AnyOfArity(
        val arity: Int,
    ) : IndexKey() {
        override val isExact: Boolean get() = false
    }

    /** Any term: variables, and whatever a unificator does not want indexed. */
    data object Wildcard : IndexKey() {
        override val isExact: Boolean get() = false
    }

    companion object {
        /** Keys consistent with [Unificator.strict]: terms are equal only if they are of the same kind and value. */
        @JvmStatic
        @JsName("strict")
        fun strict(term: Term): IndexKey =
            when {
                term.isVar -> Wildcard
                term.isAtom -> OfAtom(term.castToAtom().value)
                term.isInteger -> OfInteger(term.castToInteger().value)
                term.isReal -> OfNumber(term.castToReal().value.toDouble())
                term.isStruct -> term.castToStruct().let { OfStruct(it.functor, it.arity) }
                else -> Wildcard
            }

        /** Keys consistent with [Unificator.naive]: numbers are compared by value, whatever their type. */
        @JvmStatic
        @JsName("numbersByValue")
        fun numbersByValue(term: Term): IndexKey =
            if (term.isNumber) OfNumber(term.castToNumeric().decimalValue.toDouble()) else strict(term)
    }
}
