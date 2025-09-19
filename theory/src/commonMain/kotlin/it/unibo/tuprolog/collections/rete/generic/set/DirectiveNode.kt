package it.unibo.tuprolog.collections.rete.generic.set

import it.unibo.tuprolog.collections.rete.generic.AbstractLeafReteNode
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** A leaf node containing [Directive]s */
internal data class DirectiveNode(
    override val leafElements: MutableList<Directive> = mutableListOf(),
) : AbstractLeafReteNode<Directive>() {
    override val isDirectiveNode: Boolean
        get() = true

    override fun asDirectiveNode(): DirectiveNode = this

    override val header = "Directives"

    override fun get(element: Directive): Sequence<Directive> = indexedElements.filter { it matches element }

    override fun deepCopy(): DirectiveNode = DirectiveNode(leafElements.toMutableList())
}
