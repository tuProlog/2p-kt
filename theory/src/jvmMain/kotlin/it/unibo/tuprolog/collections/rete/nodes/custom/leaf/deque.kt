package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

import java.util.*

actual fun <T> dequeOf(vararg items: T): MutableList<T> {
    return LinkedList(listOf(*items))
}

actual fun <T> MutableList<T>.addFirst(item: T) {
    if (this is LinkedList) {
        this.addFirst(item)
    } else {
        this.add(0, item)
    }
}