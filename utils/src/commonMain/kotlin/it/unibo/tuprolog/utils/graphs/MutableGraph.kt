package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.MutableGraphImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A mutable variant of [Graph]: [add]/[remove] (and their operator aliases [plusAssign]/[minusAssign])
 * change this instance in place, unlike the copy-on-write [Graph.plus]/[Graph.minus]. This is the type to
 * reach for when building up a graph incrementally, or when applying many changes in a row without paying
 * the cost of a defensive copy for each of them (as [Graph.plus]/[Graph.minus] would); see [Graph.build] and
 * [MutableGraph.build] to construct one via a builder block:
 * ```kotlin
 * val g = MutableGraph.build<String, Int> {
 *     this += nodeOf("a")
 *     connect(nodeOf("a"), nodeOf("b"), weight = 1, bidirectional = true)
 * }
 * ```
 * @param T is the type of the payload carried by this graph's nodes
 * @param W is the type of this graph's edge weights
 */
interface MutableGraph<T, W> : Graph<T, W> {
    /** Returns an independent, mutable copy of this graph (further changes to either do not affect the other). */
    @JsName("clone")
    fun clone(): MutableGraph<T, W>

    /** Returns an immutable [Graph] snapshot of this graph's current content. */
    @JsName("toImmutable")
    fun toImmutable(): Graph<T, W>

    /** Adds [node] to this graph, in place. Does nothing if [node] already belongs to this graph. */
    fun add(node: Node<T>)

    /**
     * Adds [edge] to this graph, in place (implicitly adding its endpoints, if missing), merging it with
     * any other outgoing edge `edge.source` already has (unlike the immutable [Graph.plus], which currently
     * discards them).
     */
    fun add(edge: Edge<T, W>)

    /** Operator alias for `add(node)`. */
    @JsName("plusAssignNode")
    operator fun plusAssign(node: Node<T>)

    /** Operator alias for `add(edge)`. */
    @JsName("plusAssignEdge")
    operator fun plusAssign(edge: Edge<T, W>)

    /** Operator alias for `remove(node)`. */
    @JsName("minusAssignNode")
    operator fun minusAssign(node: Node<T>)

    /** Operator alias for `remove(edge)`. */
    @JsName("minusAssignEdge")
    operator fun minusAssign(edge: Edge<T, W>)

    override fun plus(node: Node<T>): MutableGraph<T, W>

    override fun plus(edge: Edge<T, W>): MutableGraph<T, W>

    override fun minus(node: Node<T>): MutableGraph<T, W>

    override fun minus(edge: Edge<T, W>): MutableGraph<T, W>

    /**
     * Sets (adding if missing, overwriting if present) the weight of the edge going from `edge.first` to
     * `edge.second` to [weight], in place.
     */
    @JsName("set")
    operator fun set(
        edge: Pair<Node<T>, Node<T>>,
        weight: W,
    )

    /**
     * Adds, in place, an edge from [node1] to [node2] with the given [weight] (`null` by default); if
     * [bidirectional] is `true` (`false` by default), an edge from [node2] back to [node1], with the same
     * [weight], is added as well.
     */
    @JsName("connect")
    fun connect(
        node1: Node<T>,
        node2: Node<T>,
        weight: W? = null,
        bidirectional: Boolean = false,
    )

    /** Removes [node] (and every edge incident to it), in place. Does nothing if [node] does not belong to this graph. */
    fun remove(node: Node<T>)

    /** Removes [edge] (matched by source and destination only, regardless of weight), in place, keeping its endpoints. */
    fun remove(edge: Edge<T, W>)

    companion object {
        /** Builds a new [MutableGraph] by applying [builder] to a fresh, empty instance. */
        @JsName("build")
        @JvmStatic
        fun <T, W> build(builder: MutableGraph<T, W>.() -> Unit): MutableGraph<T, W> =
            MutableGraphImpl<T, W>().also(builder)

        /** Returns a new, empty [MutableGraph] (no nodes, no edges). */
        @JsName("empty")
        @JvmStatic
        fun <T, W> empty(): MutableGraph<T, W> = MutableGraphImpl()

        /** Creates a new [MutableGraph] containing exactly [edge] (and any [edges]), and their endpoint nodes. */
        @JsName("of")
        fun <T, W> of(
            edge: Edge<T, W>,
            vararg edges: Edge<T, W>,
        ): MutableGraph<T, W> = of(listOf(edge, *edges))

        /** Creates a new [MutableGraph] containing exactly the given [edges], and their endpoint nodes. */
        @JsName("ofIterable")
        fun <T, W> of(edges: Iterable<Edge<T, W>>): MutableGraph<T, W> = MutableGraphImpl(edges)

        /** Creates a new [MutableGraph] containing exactly the given [edges], and their endpoint nodes. */
        @JsName("ofSequence")
        fun <T, W> of(edges: Sequence<Edge<T, W>>): MutableGraph<T, W> = of(edges.asIterable())
    }
}
