@file:JvmName("CursorExtensions")

package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.AbstractCursor
import it.unibo.tuprolog.utils.impl.ConjunctionCursor
import it.unibo.tuprolog.utils.impl.EmptyCursor
import kotlin.jvm.JvmName

/**
 * Concatenates this cursor with [other], yielding a [Cursor] that traverses first the elements of this
 * cursor, then, once it [Cursor.isOver], the elements of [other].
 */
operator fun <T> Cursor<out T>.plus(other: Cursor<out T>): Cursor<out T> =
    when {
        other.hasNext -> ConjunctionCursor(this, other.forceCast())
        hasNext -> this
        else -> EmptyCursor
    }

/**
 * Wraps this [Iterator] into a lazy [Cursor], pulling elements from it, one by one, as the resulting cursor
 * is traversed. Equivalent to [Cursor.of].
 */
fun <T> Iterator<T>.toCursor(): Cursor<out T> = AbstractCursor.of(this)

/**
 * Returns a lazy [Cursor] traversing the elements of this [Iterable], in iteration order.
 */
fun <T> Iterable<T>.cursor(): Cursor<out T> = this.iterator().toCursor()

/**
 * Returns a lazy [Cursor] traversing the elements of this [Sequence], in iteration order.
 */
fun <T> Sequence<T>.cursor(): Cursor<out T> = this.iterator().toCursor()

/**
 * Returns a lazy [Cursor] traversing the elements of this [Array], in index order.
 */
fun <T> Array<T>.cursor(): Cursor<out T> = this.iterator().toCursor()

/**
 * Returns a lazy [Cursor] traversing the elements of this [Collection], in iteration order.
 */
fun <T> Collection<T>.cursor(): Cursor<out T> = this.iterator().toCursor()
