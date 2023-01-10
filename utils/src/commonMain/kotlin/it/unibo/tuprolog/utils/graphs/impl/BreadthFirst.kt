package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.graphs.Graph
import it.unibo.tuprolog.utils.graphs.Node
import it.unibo.tuprolog.utils.graphs.SearchStrategy
import it.unibo.tuprolog.utils.takeFirst

internal class BreadthFirst<T, W> : SearchStrategy<T, W> {
    override fun search(graph: Graph<T, W>, source: Node<T>): Sequence<Node<T>> = sequence {
        breadthFirstSearch(graph, dequeOf(source))
    }

    private suspend fun SequenceScope<Node<T>>.breadthFirstSearch(graph: Graph<T, W>, fringe: MutableList<Node<T>>) {
        var current = fringe.takeFirst()
        while (current != null) {
            yield(current)
            fringe.addAll(graph.outgoingEdges(current).map { it.destination })
            current = fringe.takeFirst()
        }
    }
}
