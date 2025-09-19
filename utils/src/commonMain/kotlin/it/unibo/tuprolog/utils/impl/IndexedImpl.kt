package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.LongIndexed

internal data class IndexedImpl<T>(
    override val index: Long,
    override val value: T,
) : LongIndexed<T> {
    override fun <R> map(mapper: (T) -> R): LongIndexed<R> = IndexedImpl(index, mapper(value))
}
