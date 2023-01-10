@file:JvmName("Deque")

package it.unibo.tuprolog.utils

import kotlin.jvm.JvmName

expect fun <T> dequeOf(vararg items: T): MutableList<T>

expect fun <T> dequeOf(items: Iterable<T>): MutableList<T>

expect fun <T> dequeOf(items: Sequence<T>): MutableList<T>

expect fun <T> MutableList<T>.addFirst(item: T)

expect fun <T> MutableList<T>.addFirst(items: Iterable<T>)

expect fun <T> MutableList<T>.addFirst(items: Sequence<T>)

expect fun <T> MutableList<T>.takeFirst(): T?
