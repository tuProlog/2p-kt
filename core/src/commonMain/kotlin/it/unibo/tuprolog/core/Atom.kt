package it.unibo.tuprolog.core

import kotlin.collections.List

interface Atom : Struct {
    override val args: Array<Term>
        get() = arrayOf()

    override val arity: Int
        get() = 0

    override val isAtom: Boolean
        get() = true

    val value: String
        get() = functor

    override val argsList: List<Term>
        get() = emptyList()


    companion object {

        fun of(value: String): Atom {
            when (value) {
                Empty.EMPTY_LIST_FUNCTOR -> return EmptyImpl
                Truth.TRUE_FUNCTOR -> return TruthImpl.True
                Truth.FAIL_FUNCTOR -> return TruthImpl.Fail
                else -> return AtomImpl(value)
            }
        }
    }
}
