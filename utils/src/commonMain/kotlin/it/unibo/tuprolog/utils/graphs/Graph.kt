package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.GraphImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * An immutable, directed, optionally-weighted graph over nodes carrying a payload of type [T], with edges
 * carrying an optional weight of type [W]. Iterating a [Graph] (it implements [Iterable]) yields its [edges].
 *
 * Being immutable, every structural change (see [plus]/[minus]) returns a *new* [Graph] rather than mutating
 * the receiver; use [toMutable] (or build directly via [MutableGraph.build]) when many changes need to be
 * applied in a row, then [MutableGraph.toImmutable] the result back. Construct a [Graph] via [Graph.build],
 * [Graph.of], or the various node/edge-adding operators on a [MutableGraph]:
 * ```kotlin
 * val g = Graph.build<String, Int> {
 *     this += edgeOf(nodeOf("a"), nodeOf("b"), 1)
 *     connect(nodeOf("a"), nodeOf("c"), weight = 2, bidirectional = true)
 * }
 * ```
 * @param T is the type of the payload carried by this graph's nodes
 * @param W is the type of this graph's edge weights
 */
interface Graph<T, W> : Iterable<Edge<T, W>> {
    /** The set of all nodes belonging to this graph. */
    @JsName("nodes")
    val nodes: Set<Node<T>>

    /** The set of all edges belonging to this graph. */
    @JsName("edges")
    val edges: Set<Edge<T, W>>

    /** The number of nodes in this graph. */
    @JsName("size")
    val size: Int

    /** The number of edges in this graph. */
    @JsName("edgesCount")
    val edgesCount: Int

    /** Whether [node] belongs to this graph. */
    @JsName("containsNode")
    operator fun contains(node: Node<T>): Boolean

    /** Whether [edge] (matched by source, destination, *and* weight) belongs to this graph. */
    @JsName("containsEdge")
    operator fun contains(edge: Edge<T, W>): Boolean

    /** Whether this graph has an edge going from [node1] to [node2], regardless of its weight. */
    @JsName("containsEdgeAmong")
    fun containsEdgeAmong(
        node1: Node<T>,
        node2: Node<T>,
    ): Boolean

    /** Returns a [MutableGraph] copy of this graph, which can be modified in place. */
    @JsName("toMutable")
    fun toMutable(): MutableGraph<T, W>

    /** Returns the weight of the edge going from `edge.first` to `edge.second`, or `null` if there is none. */
    @JsName("get")
    operator fun get(edge: Pair<Node<T>, Node<T>>): W?

    /**
     * Returns a new [Graph], with [node] added to it. Note: as currently implemented, if [node] already
     * belongs to this graph, its existing outgoing edges are discarded in the result rather than preserved.
     */
    @JsName("plusNode")
    operator fun plus(node: Node<T>): Graph<T, W>

    /**
     * Returns a new [Graph], with [edge] added to it (implicitly adding its endpoints, if missing). Note:
     * as currently implemented, any other outgoing edge that `edge.source` already had in this graph is
     * discarded in the result, rather than kept alongside [edge] (unlike [MutableGraph.add], which merges
     * the new edge in without discarding existing ones).
     */
    @JsName("plusEdge")
    operator fun plus(edge: Edge<T, W>): Graph<T, W>

    /** Returns a new [Graph], with [node] (and every edge incident to it) removed from it. */
    @JsName("minusNode")
    operator fun minus(node: Node<T>): Graph<T, W>

    /** Returns a new [Graph], with [edge] removed from it (its endpoints are kept). */
    @JsName("minusEdge")
    operator fun minus(edge: Edge<T, W>): Graph<T, W>

    /**
     * Traverses this graph starting from [initialNode], according to [searchStrategy], as an [Iterable] of
     * [Visit]s (one per traversed node); each traversal of the returned [Iterable] restarts from scratch.
     */
    @JsName("asIterable")
    fun <S> asIterable(
        searchStrategy: SearchStrategy<T, W, S>,
        initialNode: Node<T>,
    ): Iterable<Visit<T, S>>

    /**
     * Same as [asIterable], but as a lazy [Sequence] of [Visit]s.
     */
    @JsName("asSequence")
    fun <S> asSequence(
        searchStrategy: SearchStrategy<T, W, S>,
        initialNode: Node<T>,
    ): Sequence<Visit<T, S>>

    /** Returns all the edges of this graph originating from [from]. */
    @JsName("outgoingEdges")
    fun outgoingEdges(from: Node<T>): Iterable<Edge<T, W>>

    /** Returns all the edges of this graph pointing to [to]. */
    @JsName("ingoingEdges")
    fun ingoingEdges(to: Node<T>): Iterable<Edge<T, W>>

    /** The number of edges of this graph pointing to [to], i.e. `ingoingEdges(to).count()`. */
    @JsName("indegree")
    fun indegree(to: Node<T>): Int

    /** The number of edges of this graph originating from [from], i.e. `outgoingEdges(from).count()`. */
    @JsName("outdegree")
    fun outdegree(from: Node<T>): Int

    companion object {
        /**
         * Builds a new, immutable [Graph] by applying [builder] to a fresh [MutableGraph], then converting
         * the result back to an immutable [Graph]. Equivalent to `MutableGraph.build(builder).toImmutable()`.
         */
        @JsName("build")
        @JvmStatic
        fun <T, W> build(builder: MutableGraph<T, W>.() -> Unit): Graph<T, W> =
            MutableGraph.build(builder).toImmutable()

        /** Creates a new [Graph] containing exactly [edge] (and any [edges]), and their endpoint nodes. */
        @JsName("of")
        fun <T, W> of(
            edge: Edge<T, W>,
            vararg edges: Edge<T, W>,
        ): Graph<T, W> = of(listOf(edge, *edges))

        /** Returns a new, empty [Graph] (no nodes, no edges). */
        @JsName("empty")
        fun <T, W> empty(): Graph<T, W> = of(emptyList())

        /** Creates a new [Graph] containing exactly the given [edges], and their endpoint nodes. */
        @JsName("ofIterable")
        fun <T, W> of(edges: Iterable<Edge<T, W>>): Graph<T, W> = GraphImpl(edges)

        /** Creates a new [Graph] containing exactly the given [edges], and their endpoint nodes. */
        @JsName("ofSequence")
        fun <T, W> of(edges: Sequence<Edge<T, W>>): Graph<T, W> = of(edges.asIterable())
    }
}
