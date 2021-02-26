package it.unibo.tuprolog.utils

import java.util.LinkedList

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

actual fun <T> MutableList<T>.addFirst(items: Iterable<T>) {
    val i = listIterator()
    for (item in items) {
        i.add(item)
    }
}

actual fun <T> MutableList<T>.addFirst(items: Sequence<T>) {
    addFirst(items.asIterable())
}

actual fun <T> MutableList<T>.takeFirst(): T? {
    if (isEmpty()) return null
    return if (this is LinkedList) {
        this.pop()
    } else {
        val first = get(0)
        removeAt(0)
        first
    }
}
