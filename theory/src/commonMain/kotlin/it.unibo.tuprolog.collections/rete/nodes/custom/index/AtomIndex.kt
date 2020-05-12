package it.unibo.tuprolog.collections.rete.nodes.custom.index

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingLeaf
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal class AtomIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingLeaf {

    private val index: MutableMap<Atom, MutableList<IndexedClause>> = mutableMapOf()
    private val atoms: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> {
        return if (clause.firstParameter().isAtom)
            index.getOrElse(clause.asInnerAtom()){ emptyList() }
                .asSequence()
                .filter { it.innerClause matches clause }
                .map { it.innerClause }
        else extractGlobalSequence(clause)
    }

    override fun assertA(clause: IndexedClause) {
        if (ordered) {
            clause.asInnerAtom().let {
                index.getOrPut(it){ dequeOf() }
                    .addFirst(clause)
            }
            atoms.addFirst(clause)
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        clause.asInnerAtom().let {
            index.getOrPut(it){ dequeOf() }
                .add(clause)
        }
        atoms.add(clause)
    }

    override fun getFirst(clause: Clause): IndexedClause? {
        if (clause.firstParameter().isAtom) {
            index[clause.asInnerAtom()].let {
                return if(it == null) null
                else extractFirst(clause, it)
            }
        }
        else {
            return extractFirst(clause, atoms)
        }
    }

    private fun extractFirst(clause: Clause, index: MutableList<IndexedClause>): IndexedClause? {
        val actualIndex = index.indexOfFirst { it.innerClause matches clause }

        return if (actualIndex == -1) null
        else index[actualIndex]
    }

    override fun getAny(clause: Clause): IndexedClause? =
        this.getFirst(clause)

    override fun getIndexed(clause: Clause): Sequence<IndexedClause> {
        return if (clause.firstParameter().isAtom)
            index.getOrElse(clause.asAtom()){ emptyList() }
                .asSequence()
                .filter { it.innerClause matches clause }
        else extractGlobalIndexedSequence(clause)
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        index[indexed.asInnerAtom()]!!.remove(indexed)
        return sequenceOf(indexed.innerClause)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {

        return if (clause.firstParameter().isAtom){
            val partialIndex = index.getOrElse(clause.asAtom()){ mutableListOf() }
            return retractFromMutableList(clause, partialIndex)
        }
        else {
            retractFromMutableList(clause, atoms)
        }
    }

    private fun retractFromMutableList(clause: Clause, index: MutableList<IndexedClause>): Sequence<IndexedClause> {
        val result = index.filter { it.innerClause matches clause }
        result.forEach { index.remove(it) }
        return result.asSequence()
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    private fun extractGlobalIndexedSequence(clause: Clause): Sequence<IndexedClause> =
        atoms.asSequence()
            .filter { it.innerClause matches clause }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause)
            .map { it.innerClause }

    private fun Clause.firstParameter(): Term =
        this.args[0]

    private fun Term.asAtom(): Atom =
        this as Atom

    private fun Clause.asInnerAtom(): Atom =
        this as Atom

    private fun IndexedClause.asInnerAtom(): Atom =
        this.innerClause.firstParameter() as Atom

}