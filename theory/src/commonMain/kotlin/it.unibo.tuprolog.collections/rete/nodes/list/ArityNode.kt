package it.unibo.tuprolog.collections.rete.nodes.list

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.collections.rete.AbstractLeafReteNode
import it.unibo.tuprolog.unify.Unificator.Companion.matches

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
}
