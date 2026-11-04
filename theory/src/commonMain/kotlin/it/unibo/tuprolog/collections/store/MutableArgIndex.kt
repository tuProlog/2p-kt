package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.unify.IndexKey

/** An argument index of a [MutableProcedure], maintained on insertion once built. */
internal class MutableArgIndex(
    val buckets: HashMap<IndexKey, EntryDeque>,
    val wildcards: EntryDeque,
)
