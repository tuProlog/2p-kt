package it.unibo.tuprolog.collections.store

/** What [searchPositions] does after visiting a position. */
internal enum class Visit {
    /** Go on with the next position, without looking into this one. */
    SKIP,

    /** Look into the arguments of the compound term at this position too. */
    DESCEND,

    /** Stop: no other position can select fewer candidates. */
    STOP,
}
