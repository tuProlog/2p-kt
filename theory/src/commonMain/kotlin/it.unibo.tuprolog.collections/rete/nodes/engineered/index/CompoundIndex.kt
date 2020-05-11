package it.unibo.tuprolog.collections.rete.nodes.engineered.index

import it.unibo.tuprolog.collections.rete.nodes.engineered.FamilyFunctorNode
import it.unibo.tuprolog.collections.rete.nodes.engineered.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.engineered.IndexingLeaf
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal sealed class CompoundIndex(
    private val globalSelector: Struct,
    private val nestingLevel: Int
)
    : IndexingLeaf{

    protected val indexedFunctors: MutableMap<String, FamilyFunctorNode> = mutableMapOf()

    override fun get(clause: Clause): Sequence<Clause> {
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

    override fun retractAll(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    private fun Struct.currentLevelFirstParameter(): Term =
        this.firstArguments().drop(nestingLevel).first()

    private fun Struct.firstArguments(): Sequence<Term> {
        return sequence {
            var currentTerm: Term = this@firstArguments
            while (currentTerm is Struct) {
                yield(currentTerm)
                currentTerm = currentTerm[0]
            }
            yield(currentTerm)
        }
    }

    internal class OrderedIndex(
        private val globalSelector: Struct,
        private val nestingLevel: Int
    ) : CompoundIndex(globalSelector, nestingLevel) {

        override fun assertA(clause: IndexedClause) =
            indexedFunctors.getOrPut(clause)

        override fun assertZ(clause: IndexedClause) {
            TODO("Not yet implemented")
        }

    }

    internal class UnorderedIndex(
        private val globalSelector: Struct,
        private val nestingLevel: Int
    ) : CompoundIndex(globalSelector, nestingLevel) {

        override fun assertA(clause: IndexedClause) {
            TODO("Not yet implemented")
        }

        override fun assertZ(clause: IndexedClause) {
            TODO("Not yet implemented")
        }

    }

}