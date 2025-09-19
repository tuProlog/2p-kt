package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.Node

internal class GraphImpl<T, W> :
    Graph<T, W>,
    AbstractGraph<T, W, GraphImpl<T, W>> {
    constructor(edges: Iterable<Edge<T, W>>) : super(edges)

    internal constructor(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>) : super(connections)

    override fun toMutable(): MutableGraphImpl<T, W> = MutableGraphImpl(connections.toMutableMap())

    override fun newInstance(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>): GraphImpl<T, W> =
        GraphImpl(
            connections,
        )
}
