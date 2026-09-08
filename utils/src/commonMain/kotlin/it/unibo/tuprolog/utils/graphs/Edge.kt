package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.EdgeImpl
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A directed, optionally-weighted edge of a [Graph], going from [source] to [destination]; an unweighted
 * edge (as used e.g. by [Graph.build]`<T, Nothing> { ... }`) simply carries a `null` [weight]. Build one via
 * [Edge.of], or the [edgeOf] top-level shorthand. Two [Edge]s connecting the same [source] and [destination]
 * with the same [weight] are `==`-equal, since the default implementation returned by [Edge.of] is a data
 * class; a bidirectional connection is modelled as two separate [Edge]s (see [MutableGraph.connect]).
 * @param T is the type of the payload carried by the [source]/[destination] nodes
 * @param W is the type of this edge's [weight]
 */
interface Edge<T, W> {
    /** The node this edge originates from. */
    @JsName("source")
    val source: Node<T>

    /** The node this edge points to. */
    @JsName("destination")
    val destination: Node<T>

    /** This edge's weight, or `null` if the graph it belongs to is unweighted (or this edge has no weight). */
    @JsName("weight")
    val weight: W?

    companion object {
        /** Creates a new [Edge] from [source] to [destination], with an optional [weight] (`null` by default). */
        @JsName("of")
        @JvmStatic
        @JvmOverloads
        fun <T, W> of(
            source: Node<T>,
            destination: Node<T>,
            weight: W? = null,
        ): Edge<T, W> = EdgeImpl(source, destination, weight)
    }
}
