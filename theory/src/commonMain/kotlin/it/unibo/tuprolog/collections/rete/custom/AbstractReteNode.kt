package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractReteNode(override val unificator: Unificator) : ReteNode {
    fun <T : IndexedClause> removeAllLazily(source: MutableList<T>, clause: Clause): Sequence<T> =
        sequence {
            val iter = source.iterator()
            while (iter.hasNext()) {
                val it = iter.next()
                if (unificator.match(it.innerClause, clause)) {
                    it.invalidateAllCaches()
                    iter.remove()
                    yield(it)
                }
            }
        }
}
