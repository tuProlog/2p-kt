package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.NodeImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A node of a [Graph], wrapping a payload [value] of type [T]. A [Node] is a distinct, identity-less
 * wrapper around [value] (two [Node]s built from `==`-equal values are themselves `==`-equal, since the
 * default implementation returned by [Node.of] is a data class), so that the same graph can, e.g., use
 * plain [String]s or numbers as node payloads without those values having to implement any graph-specific
 * interface. Build one via [Node.of], or the [nodeOf] top-level shorthand.
 * @param T is the type of the payload carried by this node
 */
interface Node<T> {
    /** The payload wrapped by this node. */
    @JsName("value")
    val value: T

    companion object {
        /** Wraps [value] into a new [Node]. */
        @JsName("of")
        @JvmStatic
        fun <T> of(value: T): Node<T> = NodeImpl(value)
    }
}
