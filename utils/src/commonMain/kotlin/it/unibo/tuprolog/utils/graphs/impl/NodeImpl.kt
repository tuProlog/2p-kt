package it.unibo.tuprolog.utils.graphs.impl

import it.unibo.tuprolog.utils.graphs.Node

internal data class NodeImpl<T>(override val value: T) : Node<T>
