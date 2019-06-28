package it.unibo.tuprolog.core

interface Empty : Atom {

    companion object {
        const val EMPTY_LIST_FUNCTOR = "[]"
        const val EMPTY_SET_FUNCTOR = Set.FUNCTOR

        fun list(): EmptyList = EmptyList()
        fun set(): EmptySet = EmptySet()
    }

}

