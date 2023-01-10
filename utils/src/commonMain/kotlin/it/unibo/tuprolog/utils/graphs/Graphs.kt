package it.unibo.tuprolog.utils.graphs

import kotlin.js.JsName

@JsName("node")
fun <T> node(value: T): Node<T> = Node.of(value)

@JsName("edge")
fun <T, W> edge(node1: Node<T>, node2: Node<T>, weight: W? = null): Edge<T, W> = Edge.of(node1, node2, weight)

@JsName("visitOf")
fun <T, S> visitOf(state: S, node: Node<T>): Visit<T, S> = Visit.of(state, node)
