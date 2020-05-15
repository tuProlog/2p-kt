package it.unibo.tuprolog.collections.rete.custom.clause

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.LongIndexed

interface IndexedClause : LongIndexed<Clause> {

    val innerClause: Clause
        get() = value

    companion object {
        fun wrap(indexedClause: LongIndexed<Clause>): IndexedClause {
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
                LongIndexed.of(index, clause)
            )
        }
    }

}