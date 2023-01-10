package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.VisitImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Visit<T, S> {
    @JsName("state")
    val state: S

    @JsName("node")
    val node: Node<T>

    companion object {
        @JsName("of")
        @JvmStatic
        fun <T, S> of(state: S, node: Node<T>): Visit<T, S> = VisitImpl(state, node)
    }
}
