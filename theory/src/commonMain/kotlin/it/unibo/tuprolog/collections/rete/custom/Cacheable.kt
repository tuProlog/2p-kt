package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

/**Marks a Class as capable of managing a cache of [SituatedIndexedClause].*/
internal interface Cacheable<T> {

    /** Retrieves the cache of the current level in the indexing tree */
    fun getCache(): Sequence<T>

    /**Forcefully invalidate the node cache and all of its subtree cache*/
    fun invalidateCache()
}
