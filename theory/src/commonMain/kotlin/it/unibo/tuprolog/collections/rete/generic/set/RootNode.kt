package it.unibo.tuprolog.collections.rete.generic.set

import it.unibo.tuprolog.collections.rete.generic.AbstractIntermediateReteNode
import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.forceCast

/** The root node, of the Rete Tree indexing [Clause]s */
internal data class RootNode(
    override val children: MutableMap<String?, ReteNode<*, Clause>> = mutableMapOf(),
) : AbstractIntermediateReteNode<String?, Clause>(children) {
    override val isRootNode: Boolean
        get() = true

    override fun asRootNode(): RootNode = this

    override val header = "Root"

    override fun put(
        element: Clause,
        beforeOthers: Boolean,
    ) {
        when {
            element.isDirective -> {
                children.getOrPut(null) { DirectiveNode().forceCast<ReteNode<*, Clause>>() }
            }
            element.isRule -> {
                element.head?.functor?.let {
                    children.getOrPut(it) { FunctorNode(it).forceCast<ReteNode<*, Clause>>() }
                }
            }
            else -> null
        }?.put(element, beforeOthers)
    }

    override fun selectChildren(element: Clause): Sequence<ReteNode<*, Clause>?> =
        sequenceOf(
            when {
                element.isDirective -> children[null]
                element.isRule -> children[element.castToRule().head.functor]
                else -> null
            },
        )

    override fun removeWithLimit(
        element: Clause,
        limit: Int,
    ): Sequence<Clause> = selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): RootNode = RootNode(children.deepCopy({ it }, { it.deepCopy() }))
}
