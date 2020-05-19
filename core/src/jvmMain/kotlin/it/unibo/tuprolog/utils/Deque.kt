package it.unibo.tuprolog.utils

import java.util.*

actual fun <T> dequeOf(vararg items: T): MutableList<T> {
    return dequeOf(items.asIterable())
}

actual fun <T> dequeOf(items: Iterable<T>): MutableList<T> {
    return LinkedList<T>().also {
        for (item in items) {
            it.add(item)
        }
    }
}

actual fun <T> dequeOf(items: Sequence<T>): MutableList<T> {
    return items.toCollection(LinkedList())
}

actual fun <T> MutableList<T>.addFirst(item: T) {
    if (this is LinkedList) {
        this.addFirst(item)
    } else {
        this.add(0, item)
    }
}
