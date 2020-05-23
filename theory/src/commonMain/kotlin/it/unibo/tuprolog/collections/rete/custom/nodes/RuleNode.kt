package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal class RuleNode(
    private val ordered: Boolean
) : TopLevelReteNode {

    private val functors: MutableMap<String, FunctorRete> = mutableMapOf()
    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

    override fun get(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()

    override fun assertA(clause: IndexedClause) =
        clause.nestedFunctor().let {
            if (ordered) {
                functors.getOrPut(it) {
                    FunctorNode.TopLevelFunctorReteNode(ordered, 0)
                }.assertA(clause)
            } else {
                assertZ(clause)
            }
        }

    override fun assertZ(clause: IndexedClause) =
        clause.nestedFunctor().let {
            functors.getOrPut(it) {
                FunctorNode.TopLevelFunctorReteNode(ordered, 0)
            }.assertZ(clause)
        }

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.retractFirst(clause)?.let {
            invalidCache(it)
            it
        } ?: emptySequence()

    override fun retractAll(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.retractAll(clause)?.let {
            invalidCache(it)
            it
        } ?: emptySequence()

    override fun invalidateCache() {
        theoryCache.invalidate()
        functors.values.forEach { it.invalidateCache() }
    }

    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    private fun Clause.nestedFunctor(): String =
        this.head!!.functorOfNestedFirstArgument(0)

    private fun IndexedClause.nestedFunctor(): String =
        this.innerClause.head!!.functorOfNestedFirstArgument(0)

    private fun invalidCache(result: Sequence<*>) {
        if (result.any()) {
            theoryCache.invalidate()
        }
    }

    private fun regenerateCache(): MutableList<SituatedIndexedClause> =
        dequeOf(
            if (ordered) {
                Utils.merge(
                    functors.values.map {
                        it.getCache()
                    }
                )
            } else {
                Utils.flattenIndexed(
                    functors.values.map { outer ->
                        outer.getCache()
                    }
                )
            }
        )

}