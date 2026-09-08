package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Node
import it.unibo.tuprolog.utils.graphs.Visit

/**
 * Default [Visit] implementation returned by [Visit.of]. Not meant to be instantiated directly; use
 * [Visit.of] (or the [it.unibo.tuprolog.utils.graphs.visitOf] shorthand) instead.
 */
data class VisitImpl<T, S>(
    override val state: S,
    override val node: Node<T>,
) : Visit<T, S>
