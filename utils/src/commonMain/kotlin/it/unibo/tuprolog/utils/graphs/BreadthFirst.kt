package it.unibo.tuprolog.utils.graphs

/**
 * A [SearchStrategy] visiting a [Graph] breadth-first, i.e. level by level: all nodes at depth 0 (the
 * source), then all nodes at depth 1, and so on. Each yielded [Visit.state] is the depth (an [Int], starting
 * at 0) at which the corresponding node was reached.
 * ```kotlin
 * graph.asSequence(BreadthFirst(), sourceNode).forEach { (depth, node) -> println("$node at depth $depth") }
 * ```
 * @param maxDepth caps the traversal to nodes at depth at most [maxDepth]; a non-positive value (the
 * default, `-1`) means "unbounded"
 */
class BreadthFirst<T, W>(
    private val maxDepth: Int = -1,
) : AbstractSearchStrategy<T, W, Int>(0) {
    override fun selectNextVisit(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>,
    ): Visit<T, Int>? {
        if (maxDepth > 0 && lastTraversal.state > maxDepth) return null
        expandFringe(graph, lastTraversal, fringe)
        return lastTraversal.toVisit()
    }

    private fun expandFringe(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>,
    ) {
        fringe.addAll(
            graph.outgoingEdges(lastTraversal.destination).map {
                Traversal(lastTraversal.state + 1, it)
            },
        )
    }
}
