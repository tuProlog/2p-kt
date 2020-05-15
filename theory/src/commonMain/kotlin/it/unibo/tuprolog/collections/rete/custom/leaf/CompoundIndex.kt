package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

import it.unibo.tuprolog.collections.rete.nodes.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.nodes.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.nodes.FunctorIndexing
import it.unibo.tuprolog.collections.rete.nodes.custom.nodes.FunctorNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Var

internal class CompoundIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode{

    private val functors: MutableMap<String, FunctorIndexing> = mutableMapOf()

    override fun get(clause: Clause): Sequence<Clause> =
        if(clause.isGlobal()){
            if(ordered){
                Utils.mergeSort(
                    functors.values.asSequence().flatMap {
                        it.getIndexed(clause)
                    }
                ).map { it.innerClause }
            } else{
                Utils.merge(
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
        if(ordered) retractAllOrdered(clause)
        else retractAllUnordered(clause)

    private fun retractAllOrdered(clause: Clause): Sequence<Clause> =
        if(clause.isGlobal()) {
            Utils.mergeSort(
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
            Utils.merge(
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
            Utils.mergeSort(
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
            Utils.mergeSort(
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
            Utils.mergeSort(
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
        this.head!!.nestedFirstArgument(nestingLevel + 1) is Var

}