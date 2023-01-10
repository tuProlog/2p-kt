package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.Node

internal class ImmutableGraph<T, W> : Graph<T, W>, AbstractGraph<T, W, ImmutableGraph<T, W>> {

    constructor(edges: Iterable<Edge<T, W>>) : super(edges)

    internal constructor(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>) : super(connections)

    override fun toMutable(): MutableGraph<T, W> = MutableGraph(connections.toMutableMap())
    override fun newInstance(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>): ImmutableGraph<T, W> =
        ImmutableGraph(connections)

    override fun remove(edge: Edge<T, W>) = error("Cannot invoke this operation on immutable graph: remove(Edge)")

    override fun remove(node: Node<T>) = error("Cannot invoke this operation on immutable graph: remove(Node)")

    override fun add(edge: Edge<T, W>) = error("Cannot invoke this operation on immutable graph: add(Edge)")

    override fun add(node: Node<T>) = error("Cannot invoke this operation on immutable graph: add(Node)")
}
