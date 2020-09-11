package it.unibo.tuprolog.core

class SetIterator(Set: Set) : Iterator<Term> {

    private var current: Term? = Set

    override fun hasNext(): Boolean = current != null && current !is EmptySet

    override fun next(): Term {
        return when (val x = current) {
            is Tuple -> {
                current = x.right
                x.left
            }
            is Set -> {
                when (val y = x[0]) {
                    is Tuple -> {
                        current = y.right
                        y.left
                    }
                    else -> {
                        current = null
                        y
                    }
                }
            }
            is EmptySet, null -> throw NoSuchElementException()
            else -> {
                current = null
                x
            }
        }
    }
}
