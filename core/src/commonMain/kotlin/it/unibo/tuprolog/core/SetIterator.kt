package it.unibo.tuprolog.core

class SetIterator(set: Set) : Iterator<Term> {

    private open inner class SetIteratorVisitor : TermVisitor<Term> {
        override fun visitTuple(term: Tuple): Term {
            current = term.right
            return term.left
        }

        override fun defaultValue(term: Term): Term {
            current = null
            return term
        }
    }

    private var current: Term? = set

    override fun hasNext(): Boolean = current.let { it != null && !it.isEmptySet }

    private val innerSetIteratorVisitor = SetIteratorVisitor()

    private val outerSetIteratorVisitor = object : SetIteratorVisitor() {
        override fun visitSet(term: Set): Term =
            term[0].accept(innerSetIteratorVisitor)

        override fun visitEmptySet(term: EmptySet): Term =
            throw throw NoSuchElementException()
    }

    override fun next(): Term = current?.accept(outerSetIteratorVisitor) ?: throw NoSuchElementException()
}
