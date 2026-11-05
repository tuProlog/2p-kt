package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.unify.IndexKey

/** The index of one position ([path], see [keyAt]) of a [MutableProcedure], maintained on insertion once built. */
internal class MutableArgIndex(
    val path: Int,
    val buckets: HashMap<IndexKey, EntryDeque>,
    var wildcards: EntryDeque,
)
