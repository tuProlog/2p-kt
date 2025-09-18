package it.unibo.tuprolog.collections.rete.custom.clause

import it.unibo.tuprolog.collections.rete.custom.Cacheable
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.utils.LongIndexed

internal interface IndexedClause : LongIndexed<Clause> {
    /**Retrieves the decorated [Clause]*/
    val innerClause: Clause
        get() = value

    val traversedCacheables: List<Cacheable<*>>

    fun invalidateAllCaches()

    operator fun plus(traversed: Cacheable<*>): IndexedClause = of(index, innerClause, traversedCacheables + traversed)

    companion object {
        fun wrap(
            indexedClause: LongIndexed<Clause>,
            vararg traversed: Cacheable<*>,
        ): IndexedClause =
            object : IndexedClause {
                override val index: Long
                    get() = indexedClause.index

                override val value: Clause
                    get() = indexedClause.value

                override val traversedCacheables: List<Cacheable<*>> = listOf(*traversed)

                override fun invalidateAllCaches() {
                    traversedCacheables.forEach { it.invalidateCache() }
                }

                override fun <R> map(mapper: (Clause) -> R) = indexedClause.map(mapper)
            }

        fun of(
            index: Long,
            clause: Clause,
            vararg traversed: Cacheable<*>,
        ): IndexedClause =
            wrap(
                LongIndexed.of(index, clause),
                *traversed,
            )

        fun of(
            index: Long,
            clause: Clause,
            traversed: List<Cacheable<*>>,
        ): IndexedClause =
            wrap(
                LongIndexed.of(index, clause),
                *traversed.toTypedArray(),
            )
    }
}
