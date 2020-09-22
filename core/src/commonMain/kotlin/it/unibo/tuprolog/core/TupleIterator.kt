package it.unibo.tuprolog.core

class TupleIterator(tuple: Tuple) : Iterator<Term> {

    private var current: Term? = tuple

    override fun hasNext(): Boolean = current != null

    override fun next(): Term {
        return when (val x = current) {
            is Tuple -> {
                current = x.right
                x.left
            }
            null -> throw NoSuchElementException()
            else -> {
                current = null
                x
            }
        }
    }
}
