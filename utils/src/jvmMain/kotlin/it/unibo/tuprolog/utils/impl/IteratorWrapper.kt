package it.unibo.tuprolog.utils.impl

internal class IteratorWrapper<T>(
    private val wrapped: Iterator<T>,
) : MutableIterator<T> {
    override fun hasNext(): Boolean = wrapped.hasNext()

    override fun next(): T = wrapped.next()

    override fun remove() =
        when (wrapped) {
            is MutableIterator<T> -> wrapped.remove()
            else -> throw UnsupportedOperationException()
        }
}
