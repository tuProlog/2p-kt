package it.unibo.tuprolog.utils.impl

internal class SequenceWrapper<T>(
    private val wrapped: Sequence<T>,
) : MutableIterable<T> {
    override fun iterator(): MutableIterator<T> = IteratorWrapper(wrapped.iterator())
}
