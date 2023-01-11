@file:JvmName("Graphs")

package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName
import kotlin.jvm.JvmName

@JsName("node")
fun <T> node(value: T): Node<T> = Node.of(value)

@JsName("edge")
fun <T, W> edge(node1: Node<T>, node2: Node<T>, weight: W? = null): Edge<T, W> = Edge.of(node1, node2, weight)

@JsName("visitOf")
fun <T, S> visitOf(state: S, node: Node<T>): Visit<T, S> = Visit.of(state, node)

@JsName("isLeaf")
fun <T, W> Graph<T, W>.isLeaf(node: Node<T>): Boolean = outdegree(node) == 0

@JsName("map")
fun <T1, W1, T2, W2> Graph<T1, W1>.map(f: (Edge<T1, W1>) -> Edge<T2, W2>): Graph<T2, W2> =
    Graph.of(this.asSequence().map(f))

@JsName("filter")
fun <T, W> Graph<T, W>.filter(p: (Edge<T, W>) -> Boolean): Graph<T, W> =
    Graph.of(this.asSequence().filter(p))

@JsName("copy")
fun <T, W> Graph<T, W>.copy(f: MutableGraph<T, W>.() -> Unit): Graph<T, W> =
    toMutable().also(f).toImmutable()

val <T, W> Graph<T, W>.isTree: Boolean
    get() = nodes.all { indegree(it) <= 1 }

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
