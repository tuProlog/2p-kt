package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName

fun interface SearchStrategy<T, W> {
    @JsName("search")
    fun search(graph: Graph<T, W>, source: Node<T>): Sequence<Node<T>>
}
