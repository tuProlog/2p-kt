package it.unibo.tuprolog.core

sealed class ListIterator(list: List) : Iterator<Term> {

    protected open var current: Term? = list

    override fun hasNext(): Boolean = current != null

    override fun next(): Term {
        return when (val x = current) {
            is Cons -> {
                current = x.tail
                x.head
            }
            is EmptyList -> onEmptyList(x)
            null -> throw NoSuchElementException()
            else -> {
                current = null
                x
            }
        }
    }

    open fun onEmptyList(item: EmptyList): Term {
        current = null
        return item
    }

    class Substituting(list: List, private val unifier: Substitution.Unifier) : ListIterator(list) {
        override var current: Term?
            get() = when (val it = super.current) {
                is Var -> unifier[it] ?: it
                else -> it
            }
            set(value) {
                super.current = value
            }
    }

    class SkippingLast(list: List) : ListIterator(list) {
        override fun hasNext(): Boolean =
            current != null && current !is EmptyList

        override fun onEmptyList(item: EmptyList): Term =
            throw NoSuchElementException()
    }

    class All(list: List) : ListIterator(list)
}
