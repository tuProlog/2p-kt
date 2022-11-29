package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.AbstractReteNode
import it.unibo.tuprolog.collections.rete.custom.ReteNode
import it.unibo.tuprolog.unify.Unificator

internal abstract class FunctorNode(unificator: Unificator) : ReteNode, AbstractReteNode(unificator)
