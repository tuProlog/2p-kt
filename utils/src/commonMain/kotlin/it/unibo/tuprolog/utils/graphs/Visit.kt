package it.unibo.tuprolog.utils.graphs

import it.unibo.tuprolog.utils.graphs.impl.VisitImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * One step of a graph traversal, as produced by a [SearchStrategy]: the [node] reached by the traversal,
 * paired with the traversal's [state] at the time it was reached (e.g. the current depth for
 * [it.unibo.tuprolog.utils.graphs.BreadthFirst]/[it.unibo.tuprolog.utils.graphs.DepthFirst], both of which
 * use an [Int] state). [Graph.asSequence] (and [Graph.asIterable]) yield a stream of [Visit]s, one per
 * traversed node. Build one via [Visit.of], or the [visitOf] top-level shorthand.
 * @param T is the type of the payload carried by the visited [node]
 * @param S is the type of the traversal-specific [state] carried alongside [node]
 */
interface Visit<T, S> {
    /** The traversal-specific state associated with reaching [node] (e.g. the depth at which it was found). */
    @JsName("state")
    val state: S

    /** The node reached by this step of the traversal. */
    @JsName("node")
    val node: Node<T>

    companion object {
        /** Creates a new [Visit] pairing [state] with [node]. */
        @JsName("of")
        @JvmStatic
        fun <T, S> of(
            state: S,
            node: Node<T>,
        ): Visit<T, S> = VisitImpl(state, node)
    }
}
