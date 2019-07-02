package it.unibo.tuprolog.core

import it.unibo.tuprolog.scoping.Scope

interface Empty : Atom {

    override fun freshCopy(): Empty = this

    override fun freshCopy(scope: Scope): Empty = this

    companion object {
        const val EMPTY_LIST_FUNCTOR = "[]"
        const val EMPTY_SET_FUNCTOR = Set.FUNCTOR

        fun list(): EmptyList = EmptyList()
        fun set(): EmptySet = EmptySet()
    }
}
