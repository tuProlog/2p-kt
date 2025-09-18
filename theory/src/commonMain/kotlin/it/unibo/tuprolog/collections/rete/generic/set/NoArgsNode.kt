package it.unibo.tuprolog.collections.rete.generic.set

import it.unibo.tuprolog.collections.rete.generic.AbstractIntermediateReteNode
import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.core.Rule

/** An intermediate node indexing Rules with no-args' heads */
internal data class NoArgsNode(
    override val children: MutableMap<Nothing?, ReteNode<*, Rule>> = mutableMapOf(),
) : AbstractIntermediateReteNode<Nothing?, Rule>(children) {
    override val isNoArgsNode: Boolean
        get() = true

    override fun asNoArgsNode(): NoArgsNode = this

    override val header = "NoArguments"

    override fun put(
        element: Rule,
        beforeOthers: Boolean,
    ) = children
        .getOrPut(null) { RuleNode() }
        .put(element, beforeOthers)

    override fun selectChildren(element: Rule): Sequence<ReteNode<*, Rule>?> = sequenceOf(children[null])

    override fun removeWithLimit(
        element: Rule,
        limit: Int,
    ): Sequence<Rule> = selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): NoArgsNode =
        NoArgsNode(
            children.deepCopy(
                { it },
                { it.deepCopy() },
            ),
        )
}
