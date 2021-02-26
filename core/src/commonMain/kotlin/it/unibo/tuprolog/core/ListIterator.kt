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

    sealed class Substituting(list: List, protected val unifier: Substitution.Unifier) : ListIterator(list) {
        final override var current: Term?
            get() = super.current.let { it?.apply(unifier) ?: it }
            set(value) {
                super.current = value
            }

        class All(list: List, unifier: Substitution.Unifier) : Substituting(list, unifier)

        class SkippingLast(list: List, unifier: Substitution.Unifier) : Substituting(list, unifier) {
            override fun hasNext(): Boolean = hasNextSkippingLast()
            override fun onEmptyList(item: EmptyList): Term = onEmptyListSkippingLast(item)
        }
    }

    class SkippingLast(list: List) : ListIterator(list) {
        override fun hasNext(): Boolean = hasNextSkippingLast()
        override fun onEmptyList(item: EmptyList): Term = onEmptyListSkippingLast(item)
    }

    class All(list: List) : ListIterator(list)

    companion object {
        private fun ListIterator.hasNextSkippingLast(): Boolean = current != null && current !is EmptyList

        @Suppress("UNUSED_PARAMETER")
        private fun ListIterator.onEmptyListSkippingLast(item: EmptyList): Term = throw NoSuchElementException()
    }
}
