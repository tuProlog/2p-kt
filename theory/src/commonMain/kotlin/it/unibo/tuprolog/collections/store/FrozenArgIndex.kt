package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/**
 * The index of one argument of a [FrozenProcedure]: for each exact key, the positions of the clauses having that key
 * as argument, plus the positions of those whose argument has no exact key. All arrays are sorted. Immutable once
 * built: its fields being `val`s, it can be published to other threads through a data race.
 */
internal class FrozenArgIndex private constructor(
    val buckets: Map<IndexKey, IntArray>,
    val wildcards: IntArray,
) {
    companion object {
        fun build(
            clauses: Array<Clause>,
            argument: Int,
            depth: Int,
            unificator: Unificator,
        ): FrozenArgIndex? {
            val buckets = HashMap<IndexKey, MutableList<Int>>()
            val wildcards = mutableListOf<Int>()
            for (i in clauses.indices) {
                val key = unificator.nestedKey(clauses[i].head!!, argument, depth)
                if (key.isExact) buckets.getOrPut(key) { mutableListOf() }.add(i) else wildcards.add(i)
            }
            if (buckets.isEmpty()) return null
            return FrozenArgIndex(buckets.mapValues { it.value.toIntArray() }, wildcards.toIntArray())
        }
    }
}
