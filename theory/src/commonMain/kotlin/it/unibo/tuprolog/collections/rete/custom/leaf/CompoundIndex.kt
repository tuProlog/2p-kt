package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.custom.nodes.FunctorIndexing
import it.unibo.tuprolog.collections.rete.custom.nodes.FunctorNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.dequeOf

internal class CompoundIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode{

    private val functors: MutableMap<String, FunctorIndexing> = mutableMapOf()

    private val cache: MutableList<SituatedIndexedClause> = dequeOf()
    private var isCacheValid: Boolean = true

    override fun getGlobalFirstIndexed(clause: Clause): SituatedIndexedClause? {
        TODO("Not yet implemented")
    }

    override fun getGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractAllGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        TODO("Not yet implemented")
    }

    override fun get(clause: Clause): Sequence<Clause> =
        if(clause.isGlobal()){
            if(ordered){
                Utils.merge(
                    functors.values.asSequence().flatMap {
                        it.getIndexed(clause)
                    }
                ).map { it.innerClause }
            } else{
                Utils.flatten(
                    functors.values.asSequence().flatMap {
                        it.get(clause)
                    }
                )
            }
        } else {
            functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()
        }

    override fun assertA(clause: IndexedClause) =
        clause.nestedFunctor().let {
            if(ordered){
                functors.getOrPut(it){
                    FunctorNode.FunctorIndexingNode(
                        ordered,
                        nestingLevel
                    )
                }.assertA(clause)
            } else{
                assertZ(clause)
            }
        }

    override fun assertZ(clause: IndexedClause) =
        clause.nestedFunctor().let {
            functors.getOrPut(it){
                FunctorNode.FunctorIndexingNode(
                    ordered,
                    nestingLevel
                )
            }.assertZ(clause)
        }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if(ordered) {
            retractAllOrdered(clause).let {
                invalidCache(it)
                it
            }
        }
        else {
            retractAllUnordered(clause).let {
                invalidCache(it)
                it
            }
        }

    override fun getCache(): Sequence<SituatedIndexedClause> {
        regenerateCache()
        return cache.asSequence()
    }

    private fun retractAllOrdered(clause: Clause): Sequence<Clause> =
        if(clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.retractAllIndexed(clause)
                }
            ).map { it.innerClause }
        } else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.retractAll(clause)
                ?: emptySequence()
        }

    private fun retractAllUnordered(clause: Clause): Sequence<Clause> =
        if(clause.isGlobal()) {
            Utils.flatten(
                functors.values.map {
                    it.retractAll(clause)
                }
            )
        } else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.retractAll(clause)
                ?: emptySequence()
        }

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
        if(clause.isGlobal()) {
            Utils.merge(
                sequenceOf(
                    functors.values.mapNotNull {
                        it.getFirstIndexed(clause)
                    }.asSequence()
                )
            ).firstOrNull()
        }else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.getFirstIndexed(clause)
        }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if(clause.isGlobal()){
            Utils.merge(
                functors.values.map {
                    it.getIndexed(clause)
                }
            )
        } else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.getIndexed(clause)
                ?: emptySequence()
        }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if(clause.isGlobal()){
            Utils.merge(
                functors.values.map {
                    it.retractAllIndexed(clause)
                }
            )
        } else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.retractAllIndexed(clause)
                ?: emptySequence()
        }

    private fun Clause.nestedFunctor(): String =
        this.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun IndexedClause.nestedFunctor(): String =
        this.innerClause.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun Clause.isGlobal(): Boolean =
        this.head!!.nestedFirstArgument(nestingLevel) is Var

    private fun invalidCache(result: Sequence<*>) {
        if (result.any()) {
            cache.clear()
            isCacheValid = false
        }
    }

    private fun regenerateCache() {
        if(!isCacheValid) {
            cache.addAll(
                if(ordered) {
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
            isCacheValid = true
        }
    }

}