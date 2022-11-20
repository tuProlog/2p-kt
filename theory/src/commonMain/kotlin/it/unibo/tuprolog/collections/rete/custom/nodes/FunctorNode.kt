package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.ReteNode
import it.unibo.tuprolog.unify.Unificator

internal abstract class FunctorNode(
    override val unificator: Unificator
) : ReteNode
