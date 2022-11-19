package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.EmptyCursor
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

    @JsName("isLazy")
    val isLazy: Boolean

    @JsName("map")
    fun <R> map(mapper: (T) -> R): Cursor<out R>

    @JsName("iterator")
    fun iterator(): Iterator<T>

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
        @JsName("ofSequence")
        fun <T> of(sequence: Sequence<T>): Cursor<out T> {
            return sequence.cursor()
        }

        @JvmStatic
        @JsName("empty")
        fun <T> empty(): Cursor<out T> {
            return EmptyCursor
        }
    }
}
