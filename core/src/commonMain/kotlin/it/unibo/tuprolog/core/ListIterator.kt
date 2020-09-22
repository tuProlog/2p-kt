package it.unibo.tuprolog.core

sealed class ListIterator(list: List) : Iterator<Term> {

    protected var current: Term? = list

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

    abstract fun onEmptyList(item: EmptyList): Term

    class SkippingLast(list: List) : ListIterator(list) {
        override fun hasNext(): Boolean =
            current != null && current !is EmptyList

        override fun onEmptyList(item: EmptyList): Term =
            throw NoSuchElementException()
    }

    class All(list: List) : ListIterator(list) {
        override fun onEmptyList(item: EmptyList): Term {
            current = null
            return item
        }
    }
}
