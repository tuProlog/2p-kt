package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.addFirst

/**
 * A [SearchStrategy] visiting a [Graph] depth-first: from the source, it follows one outgoing edge as deep
 * as possible before backtracking to explore the next one. Each yielded [Visit.state] is the depth (an
 * [Int], starting at 0) at which the corresponding node was reached.
 * ```kotlin
 * graph.asSequence(DepthFirst(), sourceNode).forEach { (depth, node) -> println("$node at depth $depth") }
 * ```
 * @param maxDepth caps the traversal to nodes at depth at most [maxDepth]; a non-positive value (the
 * default, `-1`) means "unbounded"
 * @param postOrder if `true` (`false` by default), a node is yielded only after all the nodes reachable
 * from it have been (post-order traversal), rather than as soon as it is first reached (pre-order traversal)
 */
class DepthFirst<T, W>(
    private val maxDepth: Int = -1,
    private val postOrder: Boolean = false,
) : AbstractSearchStrategy<T, W, Int>(0) {
    override fun selectNextVisit(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, Int>,
        fringe: MutableList<Traversal<T, W, Int>>,
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
        vararg others: Traversal<T, W, Int>,
    ) {
        fringe.addFirst(
            graph.outgoingEdges(lastTraversal.destination).map {
                Traversal(lastTraversal.state + 1, it, false)
            } + others,
        )
    }
}
