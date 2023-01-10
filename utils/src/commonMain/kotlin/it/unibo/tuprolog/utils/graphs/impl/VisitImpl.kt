package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Node
import it.unibo.tuprolog.utils.graphs.Visit

data class VisitImpl<T, S>(override val state: S, override val node: Node<T>) : Visit<T, S>
