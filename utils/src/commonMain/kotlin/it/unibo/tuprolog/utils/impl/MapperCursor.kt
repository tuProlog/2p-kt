package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor
import kotlin.jvm.Synchronized

internal class MapperCursor<T, R>(
    wrapped: Cursor<out T>,
    mapper: (T) -> R
) : AbstractCursor<R>() {

    init {
        require(wrapped !is EmptyCursor) {
            "Cannot create a ${MapperCursor::class.simpleName} out of an ${EmptyCursor::class.simpleName}"
        }
    }

    private var wrappedCursor: Cursor<out T>? = wrapped

    private var mapperFunction: ((T) -> R)? = mapper

    private var edits: Byte = 2

    override val next: Cursor<out R> by lazy {
        wrappedCursor!!.next.map(mapperFunction!!).thenCleanMemoryIfPossible()
    }

    override val current: R? by lazy {
        wrappedCursor!!.current?.let(mapperFunction!!).thenCleanMemoryIfPossible()
    }

    @Synchronized
    private fun <T> T.thenCleanMemoryIfPossible(): T {
        if (--edits == 0.toByte()) {
            wrappedCursor = null
            mapperFunction = null
        }
        return this
    }

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false
}
