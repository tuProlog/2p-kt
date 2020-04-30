package it.unibo.tuprolog.theory.rete.nodes

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.rete.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete.ReteNode

/** The root node, of the Rete Tree indexing [Clause]s */
internal data class RootNode(override val children: MutableMap<String?, ReteNode<*, Clause>> = mutableMapOf()) :
    AbstractIntermediateReteNode<String?, Clause>(children) {

    override val header = "Root"

    override fun put(element: Clause, beforeOthers: Boolean) {
        when (element) {
            is Directive ->
                @Suppress("UNCHECKED_CAST")
                children.getOrPut(null) { DirectiveNode() as ReteNode<*, Clause> }
            is Rule -> element.head.functor.let {
                @Suppress("UNCHECKED_CAST")
                children.getOrPut(it) { FunctorNode(it) as ReteNode<*, Clause> }
            }
            else -> null
        }?.put(element, beforeOthers)
    }

    override fun selectChildren(element: Clause): Sequence<ReteNode<*, Clause>?> =
        sequenceOf(
            when (element) {
                is Directive -> children[null]
                is Rule -> children[element.head.functor]
                else -> null
            }
        )

    override fun removeWithLimit(element: Clause, limit: Int): Sequence<Clause> =
        selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): RootNode = RootNode(children.deepCopy({ it }, { it.deepCopy() }))
}
