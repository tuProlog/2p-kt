package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Edge
import it.unibo.tuprolog.utils.graphs.Node

internal data class EdgeImpl<T, W>(
    override val source: Node<T>,
    override val destination: Node<T>,
    override val weight: W?
) : Edge<T, W>
