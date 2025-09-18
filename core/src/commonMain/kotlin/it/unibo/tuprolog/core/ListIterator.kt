package it.unibo.tuprolog.core

sealed class ListIterator(
    list: List,
) : Iterator<Term> {
    protected open var current: Term? = list

    override fun hasNext(): Boolean = current != null

    private val listIteratorVisitor =
        object : TermVisitor<Term> {
            override fun visitCons(term: Cons): Term {
                current = term.tail
                return term.head
            }

            override fun visitEmptyList(term: EmptyList): Term = onEmptyList(term)

            override fun defaultValue(term: Term): Term {
                current = null
                return term
            }
        }

    override fun next(): Term = current?.accept(listIteratorVisitor) ?: throw NoSuchElementException()

    open fun onEmptyList(item: EmptyList): Term {
        current = null
        return item
    }

    sealed class Substituting(
        list: List,
        protected val unifier: Substitution.Unifier,
    ) : ListIterator(list) {
        final override var current: Term?
            get() = super.current.let { it?.apply(unifier) ?: it }
            set(value) {
                super.current = value
            }

        class All(
            list: List,
            unifier: Substitution.Unifier,
        ) : Substituting(list, unifier)

        class SkippingLast(
            list: List,
            unifier: Substitution.Unifier,
        ) : Substituting(list, unifier) {
            override fun hasNext(): Boolean = hasNextSkippingLast()

            override fun onEmptyList(item: EmptyList): Term = onEmptyListSkippingLast(item)
        }
    }

    class SkippingLast(
        list: List,
    ) : ListIterator(list) {
        override fun hasNext(): Boolean = hasNextSkippingLast()

        override fun onEmptyList(item: EmptyList): Term = onEmptyListSkippingLast(item)
    }

    class All(
        list: List,
    ) : ListIterator(list)

    companion object {
        private fun ListIterator.hasNextSkippingLast(): Boolean = current.let { it != null && !it.isEmptyList }

        @Suppress("UNUSED_PARAMETER")
        private fun ListIterator.onEmptyListSkippingLast(item: EmptyList): Term = throw NoSuchElementException()
    }
}
