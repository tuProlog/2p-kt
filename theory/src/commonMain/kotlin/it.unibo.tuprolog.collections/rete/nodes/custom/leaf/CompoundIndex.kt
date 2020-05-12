package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

import it.unibo.tuprolog.collections.rete.nodes.custom.nodes.FunctorReteNode
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteNode
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.core.Clause

internal class CompoundIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode{

    private val functors: MutableMap<String, FunctorReteNode> = mutableMapOf()

    override fun get(clause: Clause): Sequence<Clause> =
        functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()

    override fun assertA(clause: IndexedClause) =
        clause.nestedFunctor().let {
            if(ordered){
                functors.getOrPut(it){
                    FunctorReteNode(
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
                FunctorReteNode(
                    ordered,
                    nestingLevel
                )
            }.assertZ(clause)
        }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAll(clause: Clause): Sequence<Clause> {
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


}