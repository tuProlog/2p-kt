@file:JvmName("CursorExtensions")

package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.AbstractCursor
import it.unibo.tuprolog.utils.impl.ConjunctionCursor
import it.unibo.tuprolog.utils.impl.EmptyCursor
import kotlin.jvm.JvmName

operator fun <T> Cursor<out T>.plus(other: Cursor<out T>): Cursor<out T> =
    when {
        other.hasNext -> ConjunctionCursor(this, other.forceCast())
        hasNext -> this
        else -> EmptyCursor
    }

fun <T> Iterator<T>.toCursor(): Cursor<out T> = AbstractCursor.of(this)

fun <T> Iterable<T>.cursor(): Cursor<out T> = this.iterator().toCursor()

fun <T> Sequence<T>.cursor(): Cursor<out T> = this.iterator().toCursor()

fun <T> Array<T>.cursor(): Cursor<out T> = this.iterator().toCursor()

fun <T> Collection<T>.cursor(): Cursor<out T> = this.iterator().toCursor()
