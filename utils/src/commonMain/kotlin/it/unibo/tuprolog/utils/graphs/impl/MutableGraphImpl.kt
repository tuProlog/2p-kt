package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.MutableGraph
import it.unibo.tuprolog.utils.graphs.Node

@Suppress("RedundantVisibilityModifier")
internal class MutableGraphImpl<T, W> :
    MutableGraph<T, W>,
    AbstractGraph<T, W, MutableGraphImpl<T, W>> {
    constructor(edges: Iterable<Edge<T, W>> = emptyList()) : super(edges)

    internal constructor(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>) : super(connections)

    override fun clone(): MutableGraphImpl<T, W> = MutableGraphImpl(connections.toMutableMap())

    override fun toImmutable(): Graph<T, W> = GraphImpl(connections.toMutableMap())

    public override fun remove(edge: Edge<T, W>) = removeEdge(edge)

    public override fun remove(node: Node<T>) = removeNode(node)

    public override fun add(edge: Edge<T, W>) = addEdge(edge)

    public override fun add(node: Node<T>) = addNode(node)

    override fun set(
        edge: Pair<Node<T>, Node<T>>,
        weight: W,
    ) = add(edge(edge.first, edge.second, weight))

    override fun connect(
        node1: Node<T>,
        node2: Node<T>,
        weight: W?,
        bidirectional: Boolean,
    ) {
        add(edge(node1, node2, weight))
        if (bidirectional) {
            add(edge(node2, node1, weight))
        }
    }

    override fun toMutable(): MutableGraphImpl<T, W> = clone()

    override fun plusAssign(node: Node<T>) = add(node)

    override fun plusAssign(edge: Edge<T, W>) = add(edge)

    override fun minusAssign(node: Node<T>) = remove(node)

    override fun minusAssign(edge: Edge<T, W>) = remove(edge)

    override fun newInstance(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>): MutableGraphImpl<T, W> =
        MutableGraphImpl(connections)
}
