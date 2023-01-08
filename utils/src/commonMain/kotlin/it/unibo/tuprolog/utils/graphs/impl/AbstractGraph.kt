package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.MutableGraph
import it.unibo.tuprolog.utils.graphs.Node

internal abstract class AbstractGraph<T, W> protected constructor(
    protected val connections: MutableMap<Node<T>, MutableMap<Node<T>, W>>
) : Graph<T, W> {

    companion object {
        private fun <T, W> MutableMap<Node<T>, MutableMap<Node<T>, W>>.putEdge(edge: Edge<T, W>) =
            get(edge.source)?.set(edge.destination, edge.weight)
                ?: put(edge.source, mutableMapOf(edge.destination to edge.weight))
    }

    constructor(edges: Iterable<Edge<T, W>>) : this(
        mutableMapOf<Node<T>, MutableMap<Node<T>, W>>().also {
            for (edge in edges) {
                it.putEdge(edge)
            }
        }
    )

    private data class GraphNode<T>(override val value: T) : Node<T>

    private data class GraphEdge<T, W>(
        override val source: Node<T>,
        override val destination: Node<T>,
        override val weight: W
    ) : Edge<T, W>

    override fun node(value: T): Node<T> = GraphNode(value)

    override fun edge(node1: Node<T>, node2: Node<T>, weight: W): Edge<T, W> = GraphEdge(node1, node2, weight)

    protected open fun remove(edge: Edge<T, W>) {
        connections[edge.source]?.remove(edge.destination)
    }

    protected open fun remove(node: Node<T>) {
        connections.remove(node)
    }

    protected open fun add(edge: Edge<T, W>) {
        connections.putEdge(edge)
    }

    protected open fun add(node: Node<T>) {
        if (node !in connections) {
            connections[node] = mutableMapOf()
        }
    }

    override val nodes: Set<Node<T>>
        get() = connections.keys.toSet()

    override val edges: Set<Edge<T, W>>
        get() = lazyEdges.toSet()

    protected val lazyEdges: Sequence<Edge<T, W>>
        get() = sequence {
            for ((node1, connectedNodes) in connections) {
                for ((node2, weight) in connectedNodes) {
                    yield(edge(node1, node2, weight))
                }
            }
        }

    override fun get(edge: Pair<Node<T>, Node<T>>): W? = connections[edge.first]?.get(edge.second)

    override val size: Int
        get() = connections.size

    override fun containsEdgeAmong(node1: Node<T>, node2: Node<T>): Boolean =
        connections[node1]?.containsKey(node2) == true

    override fun contains(edge: Edge<T, W>): Boolean =
        connections[edge.source]?.let {
            it.containsKey(edge.destination) && it[edge.destination] == edge.weight
        } ?: false

    override fun contains(node: Node<T>): Boolean = node in connections

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AbstractGraph<*, *>

        if (connections != other.connections) return false

        return true
    }

    override fun hashCode(): Int = connections.hashCode()

    override fun toString(): String =
        lazyEdges.joinToString(", ", "{", "}") {
            "${it.source}-${it.weight ?: ""}->${it.destination}"
        }

    override fun toMutable(): MutableGraph<T, W> = it.unibo.tuprolog.utils.graphs.impl.MutableGraph(connections)
}
