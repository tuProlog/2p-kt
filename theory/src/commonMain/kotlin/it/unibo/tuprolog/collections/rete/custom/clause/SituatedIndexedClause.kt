package it.unibo.tuprolog.collections.rete.custom.clause

import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.core.Clause

internal interface SituatedIndexedClause : IndexedClause {

    fun removeFromIndex()

    companion object {
        fun of(indexed: IndexedClause, index: Retractable): SituatedIndexedClause {
            return object :
                SituatedIndexedClause {
                override val index: Long
                    get() = indexed.index

                override val value: Clause
                    get() = indexed.value

                override fun <R> map(mapper: (Clause) -> R) =
                    indexed.map(mapper)

                override fun removeFromIndex() =
                    index.retractIndexed(this)
            }
        }
    }


}