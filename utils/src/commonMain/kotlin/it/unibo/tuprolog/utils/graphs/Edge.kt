package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName

interface Edge<T, W> {

    @JsName("source")
    val source: Node<T>

    @JsName("destination")
    val destination: Node<T>

    @JsName("weight")
    val weight: W?
}
