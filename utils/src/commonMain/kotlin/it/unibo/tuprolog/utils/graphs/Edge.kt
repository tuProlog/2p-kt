package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.EdgeImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

interface Edge<T, W> {

    @JsName("source")
    val source: Node<T>

    @JsName("destination")
    val destination: Node<T>

    @JsName("weight")
    val weight: W?

    companion object {
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun <T, W>  of(source: Node<T>, destination: Node<T>, weight: W? = null): Edge<T, W> =
            EdgeImpl(source, destination, weight)
    }
}
