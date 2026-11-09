package it.unibo.tuprolog.collections.store

/**
 * The indexes of a [FrozenProcedure], per path: a [FrozenArgIndex], or [NotIndexable]. Replaced, not updated, when an
 * index is built: as the map is only reachable through a final field, threads reading it through a data race see it
 * whole (at worst missing an index, which they build again).
 */
internal class FrozenIndexes(
    val byPath: Map<Int, Any>,
)
