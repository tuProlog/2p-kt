package it.unibo.tuprolog.utils.graphs

class DepthFirst<T, W>(
    private val postOrder: Boolean = false,
    private val maxDepth: Int = -1
) : SearchStrategy<T, W> {
    override fun search(graph: Graph<T, W>, source: Node<T>): Sequence<Node<T>> = sequence {
        depthFirstSearch(graph, source, 0)
    }

    private suspend fun SequenceScope<Node<T>>.depthFirstSearch(graph: Graph<T, W>, next: Node<T>, depth: Int) {
        if (maxDepth < 0 || depth <= maxDepth) {
            if (postOrder) {
                expand(graph, next, depth)
                yield(next)
            } else {
                yield(next)
                expand(graph, next, depth)
            }
        }
    }

    private suspend fun SequenceScope<Node<T>>.expand(graph: Graph<T, W>, next: Node<T>, depth: Int) {
        for (outgoingEdge in graph.outgoingEdges(next)) {
            val newDepth = depth + 1
            depthFirstSearch(graph, outgoingEdge.destination, newDepth)
        }
    }
}
