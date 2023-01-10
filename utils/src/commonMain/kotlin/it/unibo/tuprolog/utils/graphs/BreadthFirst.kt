package it.unibo.tuprolog.utils.graphs

class BreadthFirst<T, W>(private val maxDepth: Int = -1) : AbstractSearchStrategy<T, W, Int>(0) {
    override fun selectNextVisit(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>
    ): Visit<T, Int>? {
        if (maxDepth > 0 && lastTraversal.state > maxDepth) return null
        expandFringe(graph, lastTraversal, fringe)
        return lastTraversal.toVisit()
    }

    private fun expandFringe(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>
    ) {
        fringe.addAll(
            graph.outgoingEdges(lastTraversal.destination).map {
                Traversal(lastTraversal.state + 1, it)
            }
        )
    }
}
