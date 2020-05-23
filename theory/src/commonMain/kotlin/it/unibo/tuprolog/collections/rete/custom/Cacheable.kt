package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

/**Marks a Class as capable of managing a cache of [SituatedIndexedClause].*/
internal interface Cacheable<T> {

    /** Retrieves the cache of the current level in the indexing tree */
    fun getCache(): Sequence<T>

    fun invalidateCache()

    fun <U> invalidateCacheIfNonEmpty(seq: Sequence<U>): Sequence<U> =
        sequence {
            val i = seq.iterator()

            if (i.hasNext()) {
                invalidateCache()
                yield(i.next())
            }
            while (i.hasNext()) {
                yield(i.next())
            }
        }

    fun <U> Sequence<U>.invalidatingCacheIfNonEmpty(): Sequence<U> =
        invalidateCacheIfNonEmpty(this)
    
}