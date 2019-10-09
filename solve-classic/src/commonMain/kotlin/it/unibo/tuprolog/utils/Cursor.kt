package it.unibo.tuprolog.utils

interface Cursor<T> {
    val next: Cursor<out T>

    val current: T?

    val hasNext: Boolean

    val isOver: Boolean

    fun append(other: Cursor<T>): Cursor<out T> {
        return ConjunctionCursor(this, other)
    }

    fun <R> map(mapper: (T)->R): Cursor<out R> {
        return MapperCursor(this, mapper)
    }

    companion object {

        fun <T> of(iterator: Iterator<T>): Cursor<out T> {
            return iterator.toCursor()
        }

        fun <T> empty(): Cursor<out T> {
            return EmptyCursor
        }
    }
}

sealed class AbstractCursor<T> : Cursor<T> {
    override fun toString(): String {
        return when {
            this.isOver -> "[]"
            !this.hasNext -> "[$current]"
            else -> "[$current, ...]"
        }
    }
}

fun <T> Iterator<T>.toCursor(): Cursor<out T> {
    return if (this.hasNext()) {
        LazyCursor(this)
    } else {
        EmptyCursor
    }
}

internal object EmptyCursor : AbstractCursor<Nothing>() {
    override val next: Cursor<Nothing>
        get() = throw NoSuchElementException()

    override val current: Nothing?
        get() = throw NoSuchElementException()

    override val hasNext: Boolean
        get() = false

    override val isOver: Boolean
        get() = true

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}

internal data class NonLastCursor<T>(val iterator: Iterator<T>) : AbstractCursor<T>() {

    override val next: Cursor<out T> by lazy {
        iterator.toCursor()
    }

    override val current: T = iterator.next()

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}

internal data class LazyCursor<T>(val iterator: Iterator<T>) : AbstractCursor<T>() {

    private val wrapped: Cursor<T> by lazy { NonLastCursor(iterator) }

    override val next: Cursor<out T>
        get() = wrapped.next

    override val current: T?
        get() = wrapped.current

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}

internal data class MapperCursor<T, R>(val wrapped: Cursor<out T>, val mapper: (T)->R) : AbstractCursor<R>() {
    override val next: Cursor<out R> by lazy { MapperCursor(wrapped.next, mapper) }

    override val current: R? by lazy {
        wrapped.current.let { if (it == null) null else mapper(it) }
    }

    override val hasNext: Boolean
        get() = wrapped.hasNext

    override val isOver: Boolean
        get() = wrapped.isOver

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}

internal data class ConjunctionCursor<T>(val first: Cursor<out T>, val second: Cursor<out T>) : AbstractCursor<T>() {
    override val isOver: Boolean
        get() = first.isOver && second.isOver

    override val next: Cursor<out T>
        get() = if (first.hasNext) ConjunctionCursor(first.next, second) else second

    override val current: T?
        get() = if (first.hasNext) first.current else second.current

    override val hasNext: Boolean
        get() = first.hasNext || second.hasNext

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}

fun <T> Iterable<T>.cursor(): Cursor<out T> {
    return this.iterator().toCursor()
}

fun <T> Sequence<T>.cursor(): Cursor<out T> {
    return this.iterator().toCursor()
}

fun <T> Array<T>.cursor(): Cursor<out T> {
    return this.iterator().toCursor()
}

fun <T> Collection<T>.cursor(): Cursor<out T> {
    return this.iterator().toCursor()
}

fun main(args: Array<String>) {
    var i = (1 .. Int.MAX_VALUE).asSequence().map { println("$$it"); it }.cursor().map { it * 2 }

    while (i.hasNext) {
        println(i.current)
        i = i.next
    }

}