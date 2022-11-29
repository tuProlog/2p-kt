package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.AbstractReteNode
import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.dequeOf

internal class DirectiveIndex(
    unificator: Unificator,
    private val ordered: Boolean
) : TopLevelReteNode, AbstractReteNode(unificator) {

    private val directives: MutableList<IndexedClause> = dequeOf()

    override val size: Int
        get() = directives.size

    override val isEmpty: Boolean
        get() = directives.isEmpty()

    override fun get(clause: Clause): Sequence<Clause> =
        directives
            .filter { unificator.match(it.innerClause, clause) }
            .map { it.innerClause }
            .asSequence()

    override fun assertA(clause: IndexedClause) {
        if (ordered) {
            directives.addFirst(clause + this)
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        directives.add(clause + this)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> =
        removeAllLazily(directives, clause).map { it.innerClause }.take(1).buffered()

    override fun retractAll(clause: Clause): Sequence<Clause> =
        removeAllLazily(directives, clause).map { it.innerClause }.buffered()

    override fun getCache(): Sequence<SituatedIndexedClause> =
        directives.asSequence().map {
            SituatedIndexedClause.of(
                it,
                object : Retractable {
                    override fun retractIndexed(indexed: SituatedIndexedClause) {
                        TODO(
                            "Directives are adapted as a SituatedIndexedClause, but they are not actually stored " +
                                "with this type. Given their particular semantic, try retracting directly with a proper " +
                                "query, or opening a pull request implementing this indexing class as a proper typed data " +
                                "structure"
                        )
                    }
                }
            )
        }

    override fun invalidateCache() {
        /* do nothing */
    }
}
