package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.theory.rete2.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete2.ReteNode
import it.unibo.tuprolog.unify.Unificator.Companion.matches

/** An intermediate node indexing by Rules head's arity */
internal data class ArityNode(
    private val arity: Int,
    override val children: MutableMap<Term?, ReteNode<*, Rule>> = mutableMapOf()
) : AbstractIntermediateReteNode<Term?, Rule>(children) {

    init {
        require(arity >= 0) { "ArityNode arity should be greater than or equal to 0" }
    }

    override val header = "Arity($arity)"

    override fun put(element: Rule, beforeOthers: Boolean) = when {
        element.head.arity > 0 -> {
            val headFirstArg = element.head[0]
            val child = children.getOrElse(headFirstArg) {

                children.retrieve<ArgNode> { head -> head != null && head structurallyEquals headFirstArg }
                    .singleOrNull()
            }

            child ?: ArgNode(0, headFirstArg).also { children[headFirstArg] = it }
        }

        else -> children.getOrPut(null) { NoArgsNode() }

    }.put(element, beforeOthers)

    override fun selectChildren(element: Rule) = when {
        element.head.arity > 0 ->
            children.retrieve<ArgNode> { head -> head != null && head matches element.head[0] }

        else -> sequenceOf(children[null])
    }

    override fun removeWithNonZeroLimit(element: Rule, limit: Int): Sequence<Rule> =
        selectChildren(element)
            .foldWithLimit(mutableListOf<Rule>(), limit) { yetRemoved, currentChild ->
                yetRemoved.also {
                    it += currentChild?.remove(element, limit - it.count())
                        ?: emptySequence()
                }
            }.asSequence()

    // removeAll optimized implementation w.r.t. super class
    override fun removeAll(element: Rule): Sequence<Rule> =
        selectChildren(element)
            .fold(mutableListOf<Rule>()) { yetRemoved, currentChild ->
                yetRemoved.also {
                    it += currentChild?.removeAll(element) ?: emptySequence()
                }
            }.asSequence()

    override fun deepCopy(): ArityNode = ArityNode(arity, children.deepCopy({ it }, { it.deepCopy() }))
}
