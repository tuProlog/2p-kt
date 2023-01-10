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

    @JsName("plusAssignNode")
    operator fun plusAssign(node: Node<T>)

    @JsName("plusAssignEdge")
    operator fun plusAssign(edge: Edge<T, W>)

    @JsName("minusAssignNode")
    operator fun minusAssign(node: Node<T>)

    @JsName("minusAssignEdge")
    operator fun minusAssign(edge: Edge<T, W>)

    override fun plus(node: Node<T>): MutableGraph<T, W>

    override fun plus(edge: Edge<T, W>): MutableGraph<T, W>

    override fun minus(node: Node<T>): MutableGraph<T, W>

    override fun minus(edge: Edge<T, W>): MutableGraph<T, W>

    @JsName("set")
    operator fun set(edge: Pair<Node<T>, Node<T>>, weight: W)

    @JsName("connect")
    fun connect(node1: Node<T>, node2: Node<T>, weight: W? = null, bidirectional: Boolean = false)

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

        @JsName("of")
        fun <T, W> of(edge: Edge<T, W>, vararg edges: Edge<T, W>): MutableGraph<T, W> = of(listOf(edge, *edges))

        @JsName("ofIterable")
        fun <T, W> of(edges: Iterable<Edge<T, W>>): MutableGraph<T, W> = MutableGraphImpl(edges)

        @JsName("ofSequence")
        fun <T, W> of(edges: Sequence<Edge<T, W>>): MutableGraph<T, W> = of(edges.asIterable())
    }
}
