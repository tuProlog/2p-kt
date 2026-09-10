@file:JvmName("Graphs")

package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName
import kotlin.jvm.JvmName

/** Shorthand for [Node.of]. */
@JsName("node")
fun <T> nodeOf(value: T): Node<T> = Node.of(value)

/** Shorthand for [Edge.of]. */
@JsName("edge")
fun <T, W> edgeOf(
    node1: Node<T>,
    node2: Node<T>,
    weight: W? = null,
): Edge<T, W> = Edge.of(node1, node2, weight)

/** Shorthand for [Visit.of]. */
@JsName("visitOf")
fun <T, S> visitOf(
    state: S,
    node: Node<T>,
): Visit<T, S> = Visit.of(state, node)

/** Whether [node] has no outgoing edges in this graph, i.e. `outdegree(node) == 0`. */
@JsName("isLeaf")
fun <T, W> Graph<T, W>.isLeaf(node: Node<T>): Boolean = outdegree(node) == 0

/**
 * Returns a new [Graph], obtained by transforming every edge of this graph via [f] (which may change the
 * type of the payload/weight, or entirely rewire an edge's endpoints).
 */
@JsName("map")
fun <T1, W1, T2, W2> Graph<T1, W1>.map(f: (Edge<T1, W1>) -> Edge<T2, W2>): Graph<T2, W2> =
    Graph.of(this.asSequence().map(f))

/** Returns a new [Graph], keeping only the edges of this graph that satisfy [p]. */
@JsName("filter")
fun <T, W> Graph<T, W>.filter(p: (Edge<T, W>) -> Boolean): Graph<T, W> = Graph.of(this.asSequence().filter(p))

/**
 * Returns a new, immutable [Graph], obtained by applying the in-place changes performed by [f] to a mutable
 * copy of this graph. Handy to apply several changes to an (otherwise immutable) [Graph] at once, without
 * paying the cost of a defensive copy for each individual change.
 */
@JsName("copy")
fun <T, W> Graph<T, W>.copy(f: MutableGraph<T, W>.() -> Unit): Graph<T, W> = toMutable().also(f).toImmutable()

/**
 * Whether this graph is a tree/forest, i.e. every node has at most one incoming edge. Note that this alone
 * does not rule out cycles (see [isAcyclic] for that); e.g. a single node with a self-loop satisfies this
 * check.
 */
val <T, W> Graph<T, W>.isTree: Boolean
    get() = nodes.all { indegree(it) <= 1 }

/**
 * Whether this graph contains no directed cycles, checked by repeatedly stripping away leaf nodes (nodes
 * with no outgoing edges) from a working copy until either no nodes are left (the original graph is
 * acyclic) or no leaf remains despite nodes still being present (there is a cycle among them). This makes a
 * mutable copy of the whole graph and is therefore `O(nodes + edges)`, not free to call repeatedly on a
 * large graph.
 */
val <T, W> Graph<T, W>.isAcyclic: Boolean
    get() {
        val mutableCopy = this.toMutable()
        with(mutableCopy) {
            var leaf = nodes.firstOrNull { isLeaf(it) }
            while (leaf != null) {
                this -= leaf
                leaf = nodes.firstOrNull { isLeaf(it) }
            }
            return size == 0
        }
    }
