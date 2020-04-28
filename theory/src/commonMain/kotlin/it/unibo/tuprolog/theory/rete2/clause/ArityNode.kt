package it.unibo.tuprolog.theory.rete2.clause

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.theory.rete2.AbstractIntermediateReteNode
import it.unibo.tuprolog.theory.rete2.AbstractLeafReteNode
import it.unibo.tuprolog.theory.rete2.ReteNode
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import kotlin.collections.List as KtList

/** An intermediate node indexing by Rules head's arity */
internal data class ArityNode(
    private val arity: Int,
    override val leafElements: MutableList<Rule> = mutableListOf()
) : AbstractLeafReteNode<Rule>() {

    init {
        require(arity >= 0) { "ArityNode arity should be greater than or equal to 0" }
    }

    override val header = "Arity($arity)"

    override fun get(element: Rule): Sequence<Rule> =
        indexedElements.filter { it matches element }

    override fun deepCopy(): ArityNode =
        ArityNode(arity, leafElements.toMutableList())

//    override fun selectChildren(element: Rule) =
//        when {
//            element.head.arity > 0 ->
//                childrenMap.retrieve<ArgNode> { head -> head != null && head matches element.head[0] }
//
//            else -> sequenceOf(childrenMap[null])
//        }

//    override fun removeWithLimit(element: Rule, limit: Int): Sequence<Rule> =
//        selectChildren(element)
//            .foldWithLimit(mutableListOf<Rule>(), limit) { yetRemoved, currentChild ->
//                yetRemoved.also {
//                    it += currentChild?.remove(element, limit - it.count())
//                        ?: emptySequence()
//                }
//            }.asSequence()
//
//    // removeAll optimized implementation w.r.t. super class
//    override fun removeAll(element: Rule): Sequence<Rule> =
//        selectChildren(element)
//            .fold(mutableListOf<Rule>()) { yetRemoved, currentChild ->
//                yetRemoved.also {
//                    it += currentChild?.removeAll(element) ?: emptySequence()
//                }
//            }.asSequence()

}
