package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal class RuleNode(
    override val unificator: Unificator,
    private val ordered: Boolean
) : TopLevelReteNode {

    private val functors: MutableMap<String, FunctorRete> = mutableMapOf()
    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

    override fun get(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()

    override fun assertA(clause: IndexedClause) {
        clause.nestedFunctor().let {
            if (ordered) {
                functors.getOrPut(it) {
                    TopLevelFunctorReteNode(unificator, ordered, 0)
                }.assertA(clause + this)
            } else {
                assertZ(clause)
            }
        }
    }

    override fun assertZ(clause: IndexedClause) {
        clause.nestedFunctor().let {
            functors.getOrPut(it) {
                TopLevelFunctorReteNode(unificator, ordered, 0)
            }.assertZ(clause + this)
        }
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.retractFirst(clause)
            ?: emptySequence()

    override val size: Int
        get() = functors.values.asSequence().map { it.size }.sum()

    override val isEmpty: Boolean
        get() = functors.isEmpty() || functors.values.all { it.isEmpty }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.retractAll(clause)
            ?: emptySequence()

    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    private fun Clause.nestedFunctor(): String =
        this.head!!.functorOfNestedFirstArgument(0)

    private fun IndexedClause.nestedFunctor(): String =
        this.innerClause.nestedFunctor()

    override fun invalidateCache() {
        theoryCache.invalidate()
//        functors.values.forEach { it.invalidateCache() }
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
                    functors.values.map {
                        it.getCache()
                    }
                )
            }
        )
}
