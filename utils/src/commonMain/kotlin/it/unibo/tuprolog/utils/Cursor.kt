package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.ConjunctionCursor
import it.unibo.tuprolog.utils.impl.EmptyCursor
import it.unibo.tuprolog.utils.impl.MapperCursor
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Cursor<T> {
    @JsName("next")
    val next: Cursor<out T>

    @JsName("current")
    val current: T?

    @JsName("hasNext")
    val hasNext: Boolean

    @JsName("isOver")
    val isOver: Boolean

    @JsName("append")
    fun append(other: Cursor<T>): Cursor<out T> {
        return ConjunctionCursor(this, other)
    }

    @JsName("map")
    fun <R> map(mapper: (T) -> R): Cursor<out R> {
        return MapperCursor(this, mapper)
    }

    companion object {

        @JvmStatic
        @JsName("of")
        fun <T> of(iterator: Iterator<T>): Cursor<out T> {
            return iterator.toCursor()
        }

        @JvmStatic
        @JsName("empty")
        fun <T> empty(): Cursor<out T> {
            return EmptyCursor
        }
    }
}
