package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.nodes.custom.nodes.FunctorIndexingNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Var

internal class CompoundIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode{

    private val functors: MutableMap<String, FunctorIndexingNode> = mutableMapOf()

    override fun get(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()

    override fun assertA(clause: IndexedClause) =
        clause.nestedFunctor().let {
            if(ordered){
                functors.getOrPut(it){
                    FunctorIndexingNode(
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
                FunctorIndexingNode(
                    ordered,
                    nestingLevel
                )
            }.assertZ(clause)
        }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAll(clause: Clause): Sequence<Clause> {
        return if(ordered) retractAllOrdered(clause)
        else retractAllUnordered(clause)
    }

    override fun retractAllOrdered(clause: Clause): Sequence<Clause> {
        return if(clause.isGlobal()) {
            Utils.mergeSort(
                functors.values.map {
                    it.retractAllIndexed(clause)
                }
            )
        } else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.retractAll(clause)
                ?: emptySequence()
        }
    }

    override fun retractAllUnordered(clause: Clause): Sequence<Clause> {
        return if(clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.retractAllUnordered(clause)
                }
            )
        } else {
            functors[clause.functorOfNestedFirstArgument(nestingLevel)]
                ?.retractAll(clause)
                ?: emptySequence()
        }
    }

    override fun retractFirstResult(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAnyResult(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun getFirst(clause: Clause): IndexedClause? {
        TODO("Not yet implemented")
    }

    override fun getAny(clause: Clause): IndexedClause? {
        TODO("Not yet implemented")
    }

    override fun getIndexed(clause: Clause): Sequence<IndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    private fun Clause.nestedFunctor(): String =
        this.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun IndexedClause.nestedFunctor(): String =
        this.innerClause.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun Clause.isGlobal(): Boolean =
        this.head!!.nestedFirstArgument(nestingLevel + 1) is Var

}