package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.Node
import it.unibo.tuprolog.utils.graphs.MutableGraph as IMutableGraph

@Suppress("RedundantVisibilityModifier")
internal class MutableGraph<T, W> : IMutableGraph<T, W>, AbstractGraph<T, W> {

    constructor(edges: Iterable<Edge<T, W>> = emptyList()) : super(edges)

    internal constructor(connections: MutableMap<Node<T>, MutableMap<Node<T>, W>>) : super(connections)

    override fun clone(): MutableGraph<T, W> = MutableGraph(connections.toMutableMap())

    override fun toImmutable(): Graph<T, W> = ImmutableGraph(connections.toMutableMap())

    public override fun remove(edge: Edge<T, W>) = super.remove(edge)

    public override fun remove(node: Node<T>) = super.remove(node)

    public override fun add(edge: Edge<T, W>) = super.add(edge)

    public override fun add(node: Node<T>) = super.add(node)

    override fun set(edge: Pair<Node<T>, Node<T>>, weight: W) =
        add(edge(edge.first, edge.second, weight))

    override fun connect(node1: Node<T>, node2: Node<T>, weight: W, bidirectional: Boolean) {
        add(edge(node1, node2, weight))
        if (bidirectional) {
            add(edge(node2, node1, weight))
        }
    }

    override fun toMutable(): MutableGraph<T, W> = clone()
}
