package it.unibo.tuprolog.utils

actual fun <T> dequeOf(vararg items: T): MutableList<T> = mutableListOf(*items)

actual fun <T> dequeOf(items: Iterable<T>): MutableList<T> =
    mutableListOf<T>().also {
        it.addAll(items)
    }

actual fun <T> dequeOf(items: Sequence<T>): MutableList<T> = items.toMutableList()

actual fun <T> MutableList<T>.addFirst(item: T) {
    this.add(0, item)
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
    val first = get(0)
    this.removeAt(0)
    return first
}
