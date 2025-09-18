package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.EMPTY_BLOCK_FUNCTOR
import it.unibo.tuprolog.core.Terms.EMPTY_LIST_FUNCTOR
import it.unibo.tuprolog.core.Terms.FAIL_FUNCTOR
import it.unibo.tuprolog.core.Terms.FALSE_FUNCTOR
import it.unibo.tuprolog.core.Terms.TRUE_FUNCTOR
import it.unibo.tuprolog.core.impl.AtomImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * Base type for constant, alphanumeric [Term]s, a.k.a. strings.
 * An [Atom] is at the same time a [String]-valued [Constant] and a 0-argument [Struct], whose [functor] is equal
 * to [value].
 */
interface Atom :
    Struct,
    Constant {
    override val arity: Int
        get() = 0

    override val isAtom: Boolean
        get() = true

    override val isEmptyBlock: Boolean
        get() = EMPTY_BLOCK_FUNCTOR == value

    override val isEmptyList: Boolean
        get() = EMPTY_LIST_FUNCTOR == value

    override val isTrue: Boolean
        get() = TRUE_FUNCTOR == value

    override val isFail: Boolean
        get() = FAIL_FUNCTOR == value

    override val value: String
        get() = functor

    override val args: KtList<Term>
        get() = emptyList()

    override val variables: Sequence<Var>
        get() = emptySequence()

    override fun freshCopy(): Atom

    override fun freshCopy(scope: Scope): Atom

    override fun asAtom(): Atom = this

    companion object {
        @JvmStatic
        @JsName("escapeValue")
        fun escapeValue(string: String): String = Struct.enquoteFunctor(string)

        @JvmStatic
        @JsName("escapeValueIfNecessary")
        fun escapeValueIfNecessary(string: String): String = Struct.enquoteFunctorIfNecessary(string)

        @JvmField
        val ATOM_PATTERN = Terms.ATOM_PATTERN

        @JvmStatic
        @JsName("of")
        fun of(value: String): Atom =
            when (value) {
                EMPTY_LIST_FUNCTOR -> Empty.list()
                EMPTY_BLOCK_FUNCTOR -> Empty.block()
                TRUE_FUNCTOR -> Truth.TRUE
                FAIL_FUNCTOR -> Truth.FAIL
                FALSE_FUNCTOR -> Truth.FALSE
                else -> AtomImpl(value)
            }
    }
}
