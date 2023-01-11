package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.MutableGraph
import it.unibo.tuprolog.utils.graphs.Node
import it.unibo.tuprolog.utils.graphs.SearchStrategy
import it.unibo.tuprolog.utils.graphs.Visit

internal abstract class AbstractGraph<T, W, Self : AbstractGraph<T, W, Self>> protected constructor(
    protected val connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>
) : Graph<T, W> {

    companion object {
        private fun <T, W> MutableMap<Node<T>, MutableMap<Node<T>, W?>>.putEdge(edge: Edge<T, W>) {
            val destination = edge.destination
            get(edge.source)?.set(destination, edge.weight)
                ?: put(edge.source, mutableMapOf(destination to edge.weight))
            if (destination !in this) {
                put(destination, mutableMapOf())
            }
        }

        private fun <T, W> MutableMap<Node<T>, MutableMap<Node<T>, W?>>.removeNode(node: Node<T>) {
            remove(node)
            for (connectedNode in values) {
                connectedNode.remove(node)
            }
        }
    }

    constructor(edges: Iterable<Edge<T, W>>) : this(
        mutableMapOf<Node<T>, MutableMap<Node<T>, W?>>().also {
            for (edge in edges) {
                it.putEdge(edge)
            }
        }
    )

    protected fun node(value: T): Node<T> = Node.of(value)

    protected fun edge(node1: Node<T>, node2: Node<T>, weight: W?): Edge<T, W> = Edge.of(node1, node2, weight)

    protected open fun remove(edge: Edge<T, W>) {
        connections[edge.source]?.remove(edge.destination)
    }

    protected open fun remove(node: Node<T>) {
        connections.removeNode(node)
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

    override fun iterator(): Iterator<Edge<T, W>> = lazyEdges.iterator()

    @Suppress("MemberVisibilityCanBePrivate")
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

        other as AbstractGraph<*, *, *>

        if (connections != other.connections) return false

        return true
    }

    override fun hashCode(): Int = connections.hashCode()

    override fun toString(): String =
        lazyEdges.joinToString(", ", "{", "}") {
            "${it.source.value}-${it.weight ?: ""}->${it.destination.value}"
        }

    override fun <S> asIterable(searchStrategy: SearchStrategy<T, W, S>, initialNode: Node<T>): Iterable<Visit<T, S>> =
        asSequence(searchStrategy, initialNode).asIterable()

    override fun <S> asSequence(searchStrategy: SearchStrategy<T, W, S>, initialNode: Node<T>): Sequence<Visit<T, S>> =
        searchStrategy.search(this, initialNode)

    override fun toMutable(): MutableGraph<T, W> = it.unibo.tuprolog.utils.graphs.impl.MutableGraph(connections)

    override fun ingoingEdges(to: Node<T>): Iterable<Edge<T, W>> =
        connections.asSequence()
            .filter { (_, destinations) -> to in destinations }
            .map { (source, destinations) -> edge(source, to, destinations[to]!!) }
            .asIterable()

    override fun outgoingEdges(from: Node<T>): Iterable<Edge<T, W>> =
        connections[from]?.asSequence()?.map { (to, weight) -> edge(from, to, weight) }?.asIterable() ?: emptyList()

    protected abstract fun newInstance(connections: MutableMap<Node<T>, MutableMap<Node<T>, W?>>): Self

    override fun plus(node: Node<T>): Self {
        val connections = connections.toMutableMap()
        connections[node] = mutableMapOf()
        return newInstance(connections)
    }

    override fun plus(edge: Edge<T, W>): Self {
        val connections = connections.toMutableMap()
        connections[edge.source] = mutableMapOf(edge.destination to edge.weight)
        return newInstance(connections)
    }

    override fun minus(node: Node<T>): Self {
        val connections = connections.toMutableMap()
        connections.removeNode(node)
        return newInstance(connections)
    }

    override fun minus(edge: Edge<T, W>): Self {
        val connections = connections.toMutableMap()
        connections[edge.source]?.remove(edge.destination)
        return newInstance(connections)
    }

    override val edgesCount: Int
        get() = connections.map { it.value.size }.sum()
}
