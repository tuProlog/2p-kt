package it.unibo.tuprolog.collections.rete.nodes

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.collections.rete.AbstractIntermediateReteNode
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.collections.rete.nodes.ArityNode

/** An intermediate node indexing by Rules head's functor */
internal data class FunctorNode(
    private val functor: String,
    override val children: MutableMap<Int, ReteNode<*, Rule>> = mutableMapOf()
) : AbstractIntermediateReteNode<Int, Rule>(children) {

    override val header = "Functor($functor)"

    override fun put(element: Rule, beforeOthers: Boolean) {
        if (functor == element.head.functor) {
            val a = element.head.arity
            children.getOrPut(a) { ArityNode(a) }.put(element, beforeOthers)
        }
    }

    override fun selectChildren(element: Rule) = sequenceOf(children[element.head.arity])

    override fun removeWithLimit(element: Rule, limit: Int): Sequence<Rule> =
        selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): FunctorNode = FunctorNode(functor, children.deepCopy({ it }, { it.deepCopy() }))
}
