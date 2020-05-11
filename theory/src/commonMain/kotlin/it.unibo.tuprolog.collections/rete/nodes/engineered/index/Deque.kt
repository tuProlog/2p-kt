package it.unibo.tuprolog.collections.rete.nodes.engineered.index

expect fun <T> dequeOf(vararg items: T): MutableList<T>

expect fun <T> MutableList<T>.addFirst(item: T): Unit
