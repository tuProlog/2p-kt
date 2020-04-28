package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.rete2.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete2.ReteNode

/** The root node, of the Rete Tree indexing [Clause]s */
internal data class RootNode(override val childrenMap: MutableMap<String?, ReteNode<*, Clause>> = mutableMapOf()) :
    AbstractIntermediateReteNode<String?, Clause>(childrenMap) {

    override val header = "Root"

    override fun put(element: Clause, beforeOthers: Boolean) {
        when (element) {
            is Directive ->
                @Suppress("UNCHECKED_CAST")
                childrenMap.getOrPut(null) { DirectiveNode() as ReteNode<*, Clause> }
            is Rule ->
                element.head.functor.let {
                    @Suppress("UNCHECKED_CAST")
                    childrenMap.getOrPut(it) { FunctorNode(it) as ReteNode<*, Clause> }
            }
            else -> null

        }?.put(element, beforeOthers)
    }

    override fun selectChildren(element: Clause): Sequence<ReteNode<*, Clause>?> =
        sequenceOf(
            when (element) {
                is Directive -> childrenMap[null]
                is Rule -> childrenMap[element.head.functor]
                else -> null
            }
        )

    override fun removeWithLimit(element: Clause, limit: Int): Sequence<Clause> =
        selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): RootNode = RootNode(childrenMap.deepCopy({ it }, { it.deepCopy() }))
}
