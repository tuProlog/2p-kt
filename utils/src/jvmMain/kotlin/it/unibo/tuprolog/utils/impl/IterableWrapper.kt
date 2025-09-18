package it.unibo.tuprolog.utils.impl

internal class IterableWrapper<T>(
    private val wrapped: Iterable<T>,
) : MutableIterable<T> {
    override fun iterator(): MutableIterator<T> = IteratorWrapper(wrapped.iterator())
}
