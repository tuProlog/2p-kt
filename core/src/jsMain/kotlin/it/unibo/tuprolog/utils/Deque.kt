package it.unibo.tuprolog.utils

actual fun <T> dequeOf(vararg items: T): MutableList<T> {
    return mutableListOf(*items)
}

actual fun <T> dequeOf(items: Iterable<T>): MutableList<T> {
    return mutableListOf<T>().also {
        it.addAll(items)
    }
}

actual fun <T> dequeOf(items: Sequence<T>): MutableList<T> {
    return items.toMutableList()
}

actual fun <T> MutableList<T>.addFirst(item: T) {
    this.add(0, item)
}
