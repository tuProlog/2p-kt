package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.addFirst

class DepthFirst<T, W>(
    private val maxDepth: Int = -1,
    private val postOrder: Boolean = false
) : AbstractSearchStrategy<T, W, Int>(0) {
    override fun selectNextVisit(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>
    ): Visit<T, Int>? {
        if (maxDepth > 0 && lastTraversal.state > maxDepth) return null
        if (lastTraversal.visited) return lastTraversal.toVisit()
        if (postOrder) {
            expandFringe(graph, lastTraversal, fringe, lastTraversal.copy(visited = true))
            return null
        } else {
            expandFringe(graph, lastTraversal, fringe)
            return lastTraversal.toVisit()
        }
    }

    private fun expandFringe(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>,
        vararg others: Traversal<T, W, Int>
    ) {
        fringe.addFirst(
            graph.outgoingEdges(lastTraversal.destination).map {
                Traversal(lastTraversal.state + 1, it, false)
            } + others
        )
    }
}
