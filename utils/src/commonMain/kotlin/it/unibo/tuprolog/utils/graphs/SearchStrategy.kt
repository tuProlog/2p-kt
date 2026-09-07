package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName

/**
 * A pluggable graph-traversal algorithm, generic over the kind of [state][S] it threads through the
 * traversal (e.g. the current depth, for [BreadthFirst]/[DepthFirst]). Rather than [Graph] baking in a
 * fixed set of traversal orders, it delegates to a [SearchStrategy] via [Graph.asSequence]/[Graph.asIterable],
 * so new orders (e.g. a custom best-first search) can be added without touching [Graph] itself. See
 * [AbstractSearchStrategy] for a base class handling the traversal's bookkeeping (the fringe of edges still
 * to explore), leaving concrete strategies to only decide which edge to explore next.
 * @param T is the type of the payload carried by the graph's nodes
 * @param W is the type of the graph's edge weights
 * @param S is the type of the state this strategy threads through the traversal
 */
interface SearchStrategy<T, W, S> {
    /** The state associated with the very first node of any traversal performed by [search]. */
    @JsName("initialState")
    val initialState: S

    /**
     * Lazily traverses [graph] starting from [source], according to this strategy, yielding a [Visit] for
     * each node reached along the way (including [source] itself).
     */
    @JsName("search")
    fun search(
        graph: Graph<T, W>,
        source: Node<T>,
    ): Sequence<Visit<T, S>>
}
