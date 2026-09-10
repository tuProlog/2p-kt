package it.unibo.tuprolog.solve.directives

/** Null-safe [ClausePartition.plus]: merges two possibly-`null` partitions, returning `null` only if both are. */
operator fun ClausePartition?.plus(other: ClausePartition?): ClausePartition? =
    when {
        this == null && other == null -> null
        this == null -> other
        other == null -> this
        else -> this + other
    }
