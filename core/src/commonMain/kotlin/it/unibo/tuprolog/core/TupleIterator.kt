package it.unibo.tuprolog.core

/** An [Iterator] walking the elements of a [Tuple], from [Tuple.left] to [Tuple.left], following [Tuple.right] chains. */
class TupleIterator(
    tuple: Tuple,
) : Iterator<Term> {
    private var current: Term? = tuple

    // The tuple whose left item has been yielded last: its right side is only read if iteration goes on,
    // so that iterating over the first items of a lazy tuple does not evaluate any further.
    private var yielded: Tuple? = null

    private fun moveOn() {
        yielded?.let {
            current = it.right
            yielded = null
        }
    }

    override fun hasNext(): Boolean {
        moveOn()
        return current != null
    }

    override fun next(): Term {
        moveOn()
        val term = current ?: throw NoSuchElementException()
        current = null
        return if (term is Tuple) {
            yielded = term
            term.left
        } else {
            term
        }
    }
}
