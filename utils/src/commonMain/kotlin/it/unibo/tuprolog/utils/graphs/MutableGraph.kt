package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.utils.graphs.impl.MutableGraph as MutableGraphImpl

interface MutableGraph<T, W> : Graph<T, W> {

    @JsName("clone")
    fun clone(): MutableGraph<T, W>

    @JsName("toImmutable")
    fun toImmutable(): Graph<T, W>

    @JsName("addNode")
    fun add(node: Node<T>)

    @JsName("addEdge")
    fun add(edge: Edge<T, W>)

    @JsName("set")
    operator fun set(edge: Pair<Node<T>, Node<T>>, weight: W)

    @JsName("connect")
    fun connect(node1: Node<T>, node2: Node<T>, weight: W, bidirectional: Boolean = false)

    @JsName("removeNode")
    fun remove(node: Node<T>)

    @JsName("removeEdge")
    fun remove(edge: Edge<T, W>)

    companion object {

        @JsName("build")
        @JvmStatic
        fun <T, W> build(builder: MutableGraph<T, W>.() -> Unit): MutableGraph<T, W> =
            MutableGraphImpl<T, W>().also(builder)

        @JsName("empty")
        @JvmStatic
        fun <T, W> empty(): MutableGraph<T, W> = MutableGraphImpl()
    }
}
