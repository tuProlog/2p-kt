package it.unibo.tuprolog.utils

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

    @JsName("map")
    fun <R> map(mapper: (T) -> R): Cursor<out R>

    @JsName("iterator")
    fun iterator(): Iterator<T> =
        object : Iterator<T> {
            private var current: Cursor<out T> = this@Cursor

            override fun hasNext(): Boolean = !current.isOver

            override fun next(): T {
                val result = current.current
                current = current.next
                return result!!
            }
        }

    @JsName("asIterable")
    fun asIterable(): Iterable<T> = Iterable { iterator() }

    @JsName("asSequence")
    fun asSequence(): Sequence<T> = Sequence { iterator() }

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
