package it.unibo.tuprolog.utils

expect fun <T> dequeOf(vararg items: T): MutableList<T>

expect fun <T> dequeOf(items: Iterable<T>): MutableList<T>

expect fun <T> dequeOf(items: Sequence<T>): MutableList<T>

expect fun <T> MutableList<T>.addFirst(item: T)
