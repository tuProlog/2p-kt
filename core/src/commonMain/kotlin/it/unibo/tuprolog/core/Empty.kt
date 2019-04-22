package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.List as LogicList

interface Empty : Atom {

    companion object {
        const val EMPTY_LIST_FUNCTOR = "[]"
        const val EMPTY_SET_FUNCTOR = Set.FUNCTOR

        fun list(): EmptyList = EmptyList()
        fun set(): EmptySet = EmptySet()
    }

}

