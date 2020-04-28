package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.rete2.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete2.ReteNode

/** An intermediate node indexing by Rules head's functor */
internal data class FunctorNode(
    private val functor: String,
    override val childrenMap: MutableMap<Int, ReteNode<*, Rule>> = mutableMapOf()
) : AbstractIntermediateReteNode<Int, Rule>(childrenMap) {

    override val header = "Functor($functor)"

    override fun put(element: Rule, beforeOthers: Boolean) {
        if (functor == element.head.functor) {
            val a = element.head.arity
            childrenMap.getOrPut(a) { ArityNode(a) }.put(element, beforeOthers)
        }
    }

    override fun selectChildren(element: Rule) = sequenceOf(childrenMap[element.head.arity])

    override fun removeWithLimit(element: Rule, limit: Int): Sequence<Rule> =
        selectChildren(element).single()?.remove(element, limit) ?: emptySequence()

    override fun deepCopy(): FunctorNode = FunctorNode(functor, childrenMap.deepCopy({ it }, { it.deepCopy() }))
}
