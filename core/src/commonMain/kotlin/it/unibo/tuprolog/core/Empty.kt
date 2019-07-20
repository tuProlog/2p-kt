package it.unibo.tuprolog.core

interface Empty : Atom {

    override fun freshCopy(): Empty = this

    override fun freshCopy(scope: Scope): Empty = this

    override fun <T> accept(visitor: TermVisitor<T>): T =
            visitor.visit(this)

    companion object {
        const val EMPTY_LIST_FUNCTOR = "[]"
        const val EMPTY_SET_FUNCTOR = Set.FUNCTOR

        fun list(): EmptyList = EmptyList()
        fun set(): EmptySet = EmptySet()
    }
}
