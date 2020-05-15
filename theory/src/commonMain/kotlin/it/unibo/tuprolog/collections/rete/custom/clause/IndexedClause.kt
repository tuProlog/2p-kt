package it.unibo.tuprolog.collections.rete.nodes.custom.clause

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.Indexed

interface IndexedClause : Indexed<Clause> {

    val innerClause: Clause
        get() = value

    companion object {
        fun wrap(indexedClause: Indexed<Clause>): IndexedClause {
            return object : IndexedClause {
                override val index: Long
                    get() = indexedClause.index

                override val value: Clause
                    get() = indexedClause.value

                override fun <R> map(mapper: (Clause) -> R) =
                    indexedClause.map(mapper)
            }
        }

        fun of(index: Long, clause: Clause): IndexedClause {
            return wrap(
                Indexed.of(index, clause)
            )
        }
    }

}