package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.ImmutableGraph
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Graph<T, W> {
    @JsName("nodes")
    val nodes: Set<Node<T>>

    @JsName("edges")
    val edges: Set<Edge<T, W>>

    @JsName("size")
    val size: Int

    @JsName("node")
    fun node(value: T): Node<T>

    @JsName("edge")
    fun edge(node1: Node<T>, node2: Node<T>, weight: W): Edge<T, W>

    @JsName("containsNode")
    operator fun contains(node: Node<T>): Boolean

    @JsName("containsEdge")
    operator fun contains(edge: Edge<T, W>): Boolean

    @JsName("containsEdgeAmong")
    fun containsEdgeAmong(node1: Node<T>, node2: Node<T>): Boolean

    @JsName("toMutable")
    fun toMutable(): MutableGraph<T, W>

    @JsName("get")
    operator fun get(edge: Pair<Node<T>, Node<T>>): W?

    @JsName("asIterable")
    fun asIterable(searchStrategy: SearchStrategy<T, W>, initialNode: Node<T>): Iterable<Node<T>>

    @JsName("outgoingEdges")
    fun outgoingEdges(from: Node<T>): Iterable<Edge<T, W>>

    @JsName("ingoingEdges")
    fun ingoingEdges(to: Node<T>): Iterable<Edge<T, W>>

    companion object {
        @JsName("build")
        @JvmStatic
        fun <T, W> build(builder: MutableGraph<T, W>.() -> Unit): Graph<T, W> =
            MutableGraph.build(builder).toImmutable()

        @JsName("of")
        fun <T, W> of(edge: Edge<T, W>, vararg edges: Edge<T, W>): Graph<T, W> = of(listOf(edge, *edges))

        @JsName("empty")
        fun <T, W> empty(): Graph<T, W> = of(emptyList())

        @JsName("ofIterable")
        fun <T, W> of(edges: Iterable<Edge<T, W>>): Graph<T, W> = ImmutableGraph(edges)

        @JsName("ofSequence")
        fun <T, W> of(edges: Sequence<Edge<T, W>>): Graph<T, W> = of(edges.asIterable())
    }
}
