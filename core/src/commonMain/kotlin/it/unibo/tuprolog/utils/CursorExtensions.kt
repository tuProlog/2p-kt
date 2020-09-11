@file:JvmName("CursorExtensions")

package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.ConjunctionCursor
import it.unibo.tuprolog.utils.impl.EmptyCursor
import it.unibo.tuprolog.utils.impl.LazyCursor
import kotlin.jvm.JvmName

operator fun <T> Cursor<out T>.plus(other: Cursor<out T>): Cursor<out T> {
    if (other.hasNext) {
        return ConjunctionCursor(this, other)
    } else if (hasNext) {
        return this
    } else {
        return EmptyCursor
    }
}

fun <T> Iterator<T>.toCursor(): Cursor<out T> {
    return if (this.hasNext()) {
        LazyCursor(this)
    } else {
        EmptyCursor
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
