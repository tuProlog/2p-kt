package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.ReteNode
import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.arityOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal class TopLevelFunctorReteNode(
    unificator: Unificator,
    private val ordered: Boolean,
    private val nestingLevel: Int
) : FunctorNode(unificator), FunctorRete {

    private val arities: MutableMap<Int, TopLevelReteNode> = mutableMapOf()

    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

    override val size: Int
        get() = arities.values.asSequence().map { it.size }.sum()

    override val isEmpty: Boolean
        get() = arities.isEmpty() || arities.values.all { it.isEmpty }

    override fun get(clause: Clause): Sequence<Clause> =
        selectArity(clause)?.get(clause) ?: emptySequence()

    override fun assertA(clause: IndexedClause) {
        chooseAssertionBranch(clause + this, ReteNode::assertA)
    }

    override fun assertZ(clause: IndexedClause) {
        chooseAssertionBranch(clause + this, ReteNode::assertZ)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        selectArity(clause)?.retractFirst(clause)
            ?: emptySequence()

    override fun retractAll(clause: Clause): Sequence<Clause> =
        selectArity(clause)?.retractAll(clause)
            ?: emptySequence()

    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    private fun selectArity(clause: Clause) =
        arities[clause.head!!.arityOfNestedFirstArgument(nestingLevel)]

    private fun chooseAssertionBranch(clause: IndexedClause, op: ReteNode.(IndexedClause) -> Unit) {
        clause.innerClause.head!!.arityOfNestedFirstArgument(nestingLevel).let {
            when (it) {
                0 -> arities.getOrPut(it) {
                    ZeroArityReteNode(unificator, ordered)
                }
                else -> arities.getOrPut(it) {
                    FamilyArityReteNode(unificator, ordered, nestingLevel)
                }
            }
        }.op(clause)
    }

    override fun invalidateCache() {
        theoryCache.invalidate()
        // arities.values.forEach { it.invalidateCache() }
    }

    private fun regenerateCache(): MutableList<SituatedIndexedClause> =
        dequeOf(
            if (ordered) {
                Utils.merge(
                    arities.values.map {
                        it.getCache()
                    }
                )
            } else {
                Utils.flattenIndexed(
                    arities.values.map { outer ->
                        outer.getCache()
                    }
                )
            }
        )
}
