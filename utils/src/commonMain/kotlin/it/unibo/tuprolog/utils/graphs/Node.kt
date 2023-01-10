package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.NodeImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Node<T> {
    @JsName("value")
    val value: T

    companion object {
        @JsName("of")
        @JvmStatic
        fun <T> of(value: T): Node<T> = NodeImpl(value)
    }
}
