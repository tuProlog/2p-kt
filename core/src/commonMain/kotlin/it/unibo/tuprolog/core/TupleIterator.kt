package it.unibo.tuprolog.core

class TupleIterator(
    tuple: Tuple,
) : Iterator<Term> {
    private val tupleIteratorVisitor =
        object : TermVisitor<Term> {
            override fun visitTuple(term: Tuple): Term {
                current = term.right
                return term.left
            }

            override fun defaultValue(term: Term): Term {
                current = null
                return term
            }
        }

    private var current: Term? = tuple

    override fun hasNext(): Boolean = current != null

    override fun next(): Term = current?.accept(tupleIteratorVisitor) ?: throw NoSuchElementException()
}
