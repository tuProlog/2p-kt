package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

/**Marks a Class as capable of managing a cache of [SituatedIndexedClause].*/
internal interface Cacheable {

    /**Retrieves the cache of the current level in the indexing tree*/
    fun getCache(): Sequence<SituatedIndexedClause>

}