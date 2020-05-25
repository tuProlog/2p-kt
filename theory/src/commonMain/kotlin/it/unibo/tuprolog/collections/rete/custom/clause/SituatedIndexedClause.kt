package it.unibo.tuprolog.collections.rete.custom.clause

import it.unibo.tuprolog.collections.rete.custom.Cacheable
import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.core.Clause

internal interface SituatedIndexedClause : IndexedClause {

    /**Effectfully removes this [Clause] from whatever it is situated*/
    fun removeFromIndex()

    companion object {
        fun of(indexed: IndexedClause, index: Retractable): SituatedIndexedClause {
            return object : SituatedIndexedClause {
                override val index: Long
                    get() = indexed.index

                override val value: Clause
                    get() = indexed.value

                override fun <R> map(mapper: (Clause) -> R) =
                    indexed.map(mapper)

                override fun removeFromIndex() {
                    index.retractIndexed(this)
                    invalidateAllCaches()
                }

                override val traversedCacheables: List<Cacheable<*>>
                    get() = indexed.traversedCacheables

                override fun invalidateAllCaches() {
                    indexed.invalidateAllCaches()
                }

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other !is SituatedIndexedClause) return false
                    if (this.index != other.index) return false

                    return true
                }
            }
        }
    }


}