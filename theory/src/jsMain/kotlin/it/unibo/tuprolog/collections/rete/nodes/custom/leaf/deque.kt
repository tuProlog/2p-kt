package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

actual fun <T> dequeOf(vararg items: T): MutableList<T> {
    return mutableListOf(*items)
}

actual fun <T> MutableList<T>.addFirst(item: T) {
    this.add(0, item)
}