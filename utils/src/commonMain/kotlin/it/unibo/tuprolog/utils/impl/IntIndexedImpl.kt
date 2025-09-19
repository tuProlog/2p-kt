package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.IntIndexed

internal data class IntIndexedImpl<T>(
    override val index: Int,
    override val value: T,
) : IntIndexed<T> {
    override fun <R> map(mapper: (T) -> R): IntIndexed<R> = IntIndexedImpl(index, mapper(value))
}
