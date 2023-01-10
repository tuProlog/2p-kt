package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.takeFirst

abstract class AbstractSearchStrategy<T, W, S>(override val initialState: S) : SearchStrategy<T, W, S> {

    private class InitialNode<T> : Node<T> {
        override val value: T
            get() = throw NoSuchElementException()
    }

    private val initialNode: Node<T> = InitialNode()

    protected data class Traversal<T, W, S>(
        val state: S,
        override val source: Node<T>,
        override val destination: Node<T>,
        override val weight: W?,
        val visited: Boolean = false
    ) : Edge<T, W> {
        constructor(state: S, edge: Edge<T, W>, visited: Boolean = false) :
            this(state, edge.source, edge.destination, edge.weight, visited)

        fun toVisit(): Visit<T, S> = Visit.of(state, destination)
    }

    final override fun search(graph: Graph<T, W>, source: Node<T>): Sequence<Visit<T, S>> = sequence {
        search(graph, dequeOf(Traversal(initialState, initialNode, source, null, false)))
    }

    private suspend fun SequenceScope<Visit<T, S>>.search(graph: Graph<T, W>, fringe: MutableList<Traversal<T, W, S>>) {
        var current = fringe.takeFirst()
        while (current != null) {
            selectNextVisit(graph, current, fringe)?.let { yield(it) }
            current = fringe.takeFirst()
        }
    }

    protected abstract fun selectNextVisit(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, S>,
        fringe: MutableList<Traversal<T, W, S>>
    ): Visit<T, S>?

    protected val Node<T>.isInitial: Boolean
        get() = this === initialNode
}
