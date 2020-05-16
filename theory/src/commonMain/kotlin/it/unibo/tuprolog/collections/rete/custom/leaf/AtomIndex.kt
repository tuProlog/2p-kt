package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf
import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class AtomIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingLeaf, Retractable {

    private val index: MutableMap<Atom, MutableList<SituatedIndexedClause>> = mutableMapOf()
    private val atoms: MutableList<SituatedIndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> {
        return if (clause.nestedFirstArgument().isAtom)
            index[clause.asInnerAtom()]
                ?.asSequence()
                ?.filter { it.innerClause matches clause }
                ?.map { it.innerClause }
                ?: emptySequence()
        else extractGlobalSequence(clause)
    }

    override fun assertA(clause: IndexedClause) {
        if (ordered) {
            clause.asInnerAtom().let {
                index.getOrPut(it){ dequeOf() }
                    .addFirst(SituatedIndexedClause.of(clause, this))
            }
            atoms.addFirst(SituatedIndexedClause.of(clause, this))
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        clause.asInnerAtom().let {
            index.getOrPut(it){ dequeOf() }
                .add(SituatedIndexedClause.of(clause, this))
        }
        atoms.add(SituatedIndexedClause.of(clause, this))
    }

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? {
        if (clause.nestedFirstArgument().isAtom) {
            index[clause.asInnerAtom()].let {
                return if(it == null) null
                else extractFirst(clause, it)
            }
        }
        else {
            return extractFirst(clause, atoms)
        }
    }

    private fun extractFirst(clause: Clause, index: MutableList<SituatedIndexedClause>): SituatedIndexedClause? {
        val actualIndex = index.indexOfFirst { it.innerClause matches clause }

        return if (actualIndex == -1) null
        else index[actualIndex]
    }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.nestedFirstArgument().isAtom)
            index[clause.asInnerAtom()]
                ?.asSequence()
                ?.filter { it.innerClause matches clause }
                ?: emptySequence()
        else extractGlobalIndexedSequence(clause)
    }

    override fun retractIndexed(indexed: SituatedIndexedClause) {
        index[indexed.asInnerAtom()]!!.remove(indexed)
        atoms.remove(indexed)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.nestedFirstArgument().isAtom){
            val partialIndex = index.getOrElse(clause.asAtom()){ mutableListOf() }
            return retractFromMutableList(clause, partialIndex)
        }
        else {
            retractFromMutableList(clause, atoms)
        }
    }

    private fun retractFromMutableList(clause: Clause, index: MutableList<SituatedIndexedClause>):
            Sequence<SituatedIndexedClause> {
        val result = index.filter { it.innerClause matches clause }
        result.forEach { index.remove(it) }
        return result.asSequence()
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    private fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> =
        atoms.asSequence()
            .filter { it.innerClause matches clause }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause)
            .map { it.innerClause }

    private fun Clause.nestedFirstArgument(): Term =
        this.head!!.nestedFirstArgument(nestingLevel + 1)

    private fun Term.asAtom(): Atom =
        this as Atom

    private fun Clause.asInnerAtom(): Atom =
        this.nestedFirstArgument().asAtom()

    private fun SituatedIndexedClause.asInnerAtom(): Atom =
        this.innerClause.nestedFirstArgument() as Atom

    private fun IndexedClause.asInnerAtom(): Atom =
        this.innerClause.nestedFirstArgument() as Atom

}