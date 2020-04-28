package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.rete2.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete2.ReteNode

/** An intermediate node indexing Rules with no-args' heads */
internal data class NoArgsNode(override val childrenMap: MutableMap<Nothing?, ReteNode<*, Rule>> = mutableMapOf()) :
    AbstractIntermediateReteNode<Nothing?, Rule>(childrenMap) {

    override val header = "NoArguments"

    override fun put(element: Rule, beforeOthers: Boolean) =
        childrenMap.getOrPut(null) { RuleNode() }
            .put(element, beforeOthers)

    override fun selectChildren(element: Rule): Sequence<ReteNode<*, Rule>?> =
        sequenceOf(childrenMap[null])

    override fun removeWithLimit(element: Rule, limit: Int): Sequence<Rule> =
        selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): NoArgsNode = NoArgsNode(childrenMap.deepCopy({ it }, { it.deepCopy() }))
}
