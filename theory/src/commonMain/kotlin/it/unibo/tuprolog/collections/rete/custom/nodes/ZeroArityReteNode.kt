package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.dequeOf

internal class ZeroArityReteNode(
    unificator: Unificator,
    private val ordered: Boolean
) : ArityNode(unificator), ArityRete, Retractable {

    private val atoms: MutableList<SituatedIndexedClause> =
        dequeOf()

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        removeAllLazily(atoms, clause).map { it.innerClause }.take(1).buffered()

    override val size: Int
        get() = atoms.size

    override val isEmpty: Boolean
        get() = atoms.isEmpty()

    override fun get(clause: Clause): Sequence<Clause> =
        atoms.asSequence().filter { unificator.match(it.innerClause, clause) }.map { it.innerClause }

    override fun assertA(clause: IndexedClause) {
        if (ordered) {
            atoms.addFirst(
                SituatedIndexedClause.of(
                    clause + this,
                    this
                )
            )
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        atoms.add(
            SituatedIndexedClause.of(
                clause + this,
                this
            )
        )
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        removeAllLazily(atoms, clause).map { it.innerClause }.buffered()

    override fun getCache(): Sequence<SituatedIndexedClause> {
        return atoms.asSequence()
    }

    override fun invalidateCache() {
        /* do nothing */
    }

    override fun retractIndexed(indexed: SituatedIndexedClause) {
        atoms.remove(indexed)
    }
}
