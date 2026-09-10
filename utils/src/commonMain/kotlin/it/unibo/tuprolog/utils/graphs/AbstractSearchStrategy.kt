package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.takeFirst

/**
 * A base [SearchStrategy] taking care of the common bookkeeping of a graph traversal (the "fringe" of edges
 * still to explore, and driving [search] as a lazy [Sequence]), so that concrete strategies (see
 * [BreadthFirst] and [DepthFirst]) only need to implement [selectNextVisit], deciding, at each step, which
 * edge in the fringe to visit next and how to grow the fringe from there.
 *
 * The traversal starts from a synthetic [Traversal] whose source is an internal placeholder node (see
 * [isInitial]), so that [selectNextVisit] is invoked uniformly for the very first node too.
 * @param T is the type of the payload carried by the traversed graph's nodes
 * @param W is the type of the traversed graph's edge weights
 * @param S is the type of the traversal-specific state threaded through the search (e.g. depth)
 * @param initialState the [SearchStrategy.initialState] the traversal starts from
 */
abstract class AbstractSearchStrategy<T, W, S>(
    override val initialState: S,
) : SearchStrategy<T, W, S> {
    private class InitialNode<T> : Node<T> {
        override val value: T
            get() = throw NoSuchElementException()
    }

    private val initialNode: Node<T> = InitialNode()

    /**
     * One entry of the traversal fringe: an [Edge] (from [source] to [destination]) still to be explored,
     * paired with the traversal [state] it would carry and whether it has already been [visited] once
     * (used by post-order [DepthFirst] traversals, which re-queue a node to be yielded after its subtree).
     */
    protected data class Traversal<T, W, S>(
        val state: S,
        override val source: Node<T>,
        override val destination: Node<T>,
        override val weight: W?,
        val visited: Boolean = false,
    ) : Edge<T, W> {
        constructor(state: S, edge: Edge<T, W>, visited: Boolean = false) :
            this(state, edge.source, edge.destination, edge.weight, visited)

        /** Converts this traversal step into the [Visit] to be yielded for [destination]. */
        fun toVisit(): Visit<T, S> = Visit.of(state, destination)
    }

    /**
     * Lazily traverses [graph] from [source], repeatedly delegating to [selectNextVisit] to pick which
     * fringe entry to explore next, until the fringe is empty.
     */
    final override fun search(
        graph: Graph<T, W>,
        source: Node<T>,
    ): Sequence<Visit<T, S>> =
        sequence {
            search(graph, dequeOf(Traversal(initialState, initialNode, source, null, false)))
        }

    private suspend fun SequenceScope<Visit<T, S>>.search(
        graph: Graph<T, W>,
        fringe: MutableList<Traversal<T, W, S>>,
    ) {
        var current = fringe.takeFirst()
        while (current != null) {
            selectNextVisit(graph, current, fringe)?.let { yield(it) }
            current = fringe.takeFirst()
        }
    }

    /**
     * Decides how to handle [lastTraversal], the fringe entry just popped off (typically expanding [fringe]
     * with [lastTraversal]'s destination's outgoing edges via [Graph.outgoingEdges]), and whether a [Visit]
     * should be yielded for it. Implementations decide the traversal order (e.g. breadth-first vs.
     * depth-first) by how/where they insert new entries into [fringe] (see [it.unibo.tuprolog.utils.addFirst]
     * vs. plain appending), and may return `null` to skip yielding a [Visit] for this step (e.g. to defer it
     * to a post-order re-visit, as [DepthFirst] does).
     * @return the [Visit] to yield for this step, or `null` to yield none
     */
    protected abstract fun selectNextVisit(
        graph: Graph<T, W>,
        lastTraversal: Traversal<T, W, S>,
        fringe: MutableList<Traversal<T, W, S>>,
    ): Visit<T, S>?

    /** Whether this node is the internal placeholder used as the source of the very first [Traversal]. */
    protected val Node<T>.isInitial: Boolean
        get() = this === initialNode
}
