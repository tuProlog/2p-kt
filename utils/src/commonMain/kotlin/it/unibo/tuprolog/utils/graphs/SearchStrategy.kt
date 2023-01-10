package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName

interface SearchStrategy<T, W, S> {

    @JsName("initialState")
    val initialState: S

    @JsName("search")
    fun search(graph: Graph<T, W>, source: Node<T>): Sequence<Visit<T, S>>
}
