package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.ImmutableGraph
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Graph<T, W> : Iterable<Edge<T, W>> {
    @JsName("nodes")
    val nodes: Set<Node<T>>

    @JsName("edges")
    val edges: Set<Edge<T, W>>

    @JsName("size")
    val size: Int

    @JsName("edgesCount")
    val edgesCount: Int

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

    @JsName("plusNode")
    operator fun plus(node: Node<T>): Graph<T, W>

    @JsName("plusEdge")
    operator fun plus(edge: Edge<T, W>): Graph<T, W>

    @JsName("minusNode")
    operator fun minus(node: Node<T>): Graph<T, W>

    @JsName("minusEdge")
    operator fun minus(edge: Edge<T, W>): Graph<T, W>

    @JsName("asIterable")
    fun <S> asIterable(searchStrategy: SearchStrategy<T, W, S>, initialNode: Node<T>): Iterable<Visit<T, S>>

    @JsName("asSequence")
    fun <S> asSequence(searchStrategy: SearchStrategy<T, W, S>, initialNode: Node<T>): Sequence<Visit<T, S>>

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
