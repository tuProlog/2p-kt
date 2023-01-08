package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName

interface Node<T> {
    @JsName("value")
    val value: T
}
