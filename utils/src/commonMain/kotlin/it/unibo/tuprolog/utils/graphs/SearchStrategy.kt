package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.BreadthFirst
import it.unibo.tuprolog.utils.graphs.impl.DepthFirst
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

fun interface SearchStrategy<T, W> {
    @JsName("search")
    fun search(graph: Graph<T, W>, source: Node<T>): Sequence<Node<T>>

    companion object {
        @JsName("depthFirst")
        @JvmStatic
        fun <T, W> depthFirst(postOrder: Boolean = false): SearchStrategy<T, W> = DepthFirst(postOrder)

        @JsName("limitedDepthFirst")
        @JvmStatic
        fun <T, W> limitedDepthFirst(maxDepth: Int, postOrder: Boolean = false): SearchStrategy<T, W> =
            DepthFirst(postOrder, maxDepth)

        @JsName("breadthFirst")
        @JvmStatic
        fun <T, W> breadthFirst(): SearchStrategy<T, W> = BreadthFirst()
    }
}
