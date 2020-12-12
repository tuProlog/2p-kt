package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.AtomImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Atom : Struct, Constant {

    override val args: Array<Term>
        get() = arrayOf()

    override val arity: Int
        get() = 0

    override val isAtom: Boolean
        get() = true

    override val isEmptySet: Boolean
        get() = Empty.EMPTY_SET_FUNCTOR == value

    override val isEmptyList: Boolean
        get() = Empty.EMPTY_LIST_FUNCTOR == value

    override val isTrue: Boolean
        get() = Truth.TRUE_FUNCTOR == value

    override val isFail: Boolean
        get() = Truth.FAIL_FUNCTOR == value

    override val value: String
        get() = functor

    override val argsList: kotlin.collections.List<Term>
        get() = emptyList()

    override val variables: Sequence<Var>
        get() = emptySequence()

    override fun freshCopy(): Atom = this

    override fun freshCopy(scope: Scope): Atom = this

    companion object {

        @JvmStatic
        @JsName("escapeValue")
        fun escapeValue(string: String): String =
            Struct.escapeFunctor(string)

        @JvmStatic
        @JsName("escapeValueIfNecessary")
        fun escapeValueIfNecessary(string: String): String =
            Struct.escapeFunctorIfNecessary(string)

        @JvmField
        val ATOM_REGEX_PATTERN = Struct.STRUCT_FUNCTOR_REGEX_PATTERN

        @JvmStatic
        @JsName("of")
        fun of(value: String): Atom =
            when (value) {
                Empty.EMPTY_LIST_FUNCTOR -> Empty.list()
                Empty.EMPTY_SET_FUNCTOR -> Empty.set()
                Truth.TRUE_FUNCTOR -> Truth.TRUE
                Truth.FAIL_FUNCTOR -> Truth.FAIL
                Truth.FALSE_FUNCTOR -> Truth.FALSE
                else -> AtomImpl(value)
            }
    }
}
