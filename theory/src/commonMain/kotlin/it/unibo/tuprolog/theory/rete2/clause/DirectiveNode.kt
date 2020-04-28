package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.theory.rete.AbstractLeafReteNode
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** A leaf node containing [Directive]s */
internal data class DirectiveNode(override val leafElements: MutableList<Directive> = mutableListOf()) :
    AbstractLeafReteNode<Directive>() {

    override val header = "Directives"

    override fun get(element: Directive): Sequence<Directive> = indexedElements.filter { it matches element }

    override fun deepCopy(): DirectiveNode = DirectiveNode(leafElements.toMutableList())
}
