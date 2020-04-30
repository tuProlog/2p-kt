package it.unibo.tuprolog.theory.rete.nodes.old

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.rete.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete.ReteNode

/** An intermediate node indexing Rules with no-args' heads */
internal data class NoArgsNode(override val children: MutableMap<Nothing?, ReteNode<*, Rule>> = mutableMapOf()) :
    AbstractIntermediateReteNode<Nothing?, Rule>(children) {

    override val header = "NoArguments"

    override fun put(element: Rule, beforeOthers: Boolean) =
        children.getOrPut(null) { RuleNode() }
            .put(element, beforeOthers)

    override fun selectChildren(element: Rule): Sequence<ReteNode<*, Rule>?> =
        sequenceOf(children[null])

    override fun removeWithLimit(element: Rule, limit: Int): Sequence<Rule> =
        selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): NoArgsNode =
        NoArgsNode(
            children.deepCopy({ it },
                { it.deepCopy() })
        )
}
