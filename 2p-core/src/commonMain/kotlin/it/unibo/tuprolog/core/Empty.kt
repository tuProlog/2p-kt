package it.unibo.tuprolog.core

import kotlin.collections.List

import it.unibo.tuprolog.core.List as LogicList

interface Empty : Atom, LogicList {

    override val isCouple: Boolean
        get() = false

    override val isEmptyList: Boolean
        get() = true

    override fun toArray(): Array<Term> {
        return arrayOf()
    }

    override fun toList(): List<Term> {
        return kotlin.collections.emptyList()
    }

    override fun toSequence(): Sequence<Term> {
        return emptySequence()
    }

    companion object {
        const val EMPTY_LIST_FUNCTOR = "[]"

        fun list(): Empty = EmptyImpl

//        fun set(): Empty {
//            return EmptyImpl.INSTANCE
//        }
    }
}
