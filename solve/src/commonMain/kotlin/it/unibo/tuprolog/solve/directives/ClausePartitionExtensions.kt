package it.unibo.tuprolog.solve.directives

operator fun ClausePartition?.plus(other: ClausePartition?): ClausePartition =
    when {
        this == null && other == null -> ClausePartitionImpl()
        this == null -> other!!
        other == null -> this
        else -> this + other
    }
