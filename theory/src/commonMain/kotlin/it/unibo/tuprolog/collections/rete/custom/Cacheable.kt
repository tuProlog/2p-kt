package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

/**Marks a Class as capable of managing a cache of [SituatedIndexedClause].*/
internal interface Cacheable {

    /**Forcefully invalidate the node cache and all of its subtree cache*/
    fun invalidateCache()

    /**Retrieves the cache of the current level in the indexing tree*/
    fun getCache(): Sequence<SituatedIndexedClause>

}