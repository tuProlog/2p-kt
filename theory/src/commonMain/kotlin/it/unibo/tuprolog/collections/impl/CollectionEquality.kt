package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

/** Ordered collections are equal when they hold equal clauses in the same order (theories only equal theories). */
internal fun ClauseQueue.orderedEquals(other: Any?): Boolean =
    this === other ||
        (other is ClauseQueue && other !is Theory && size == other.size && zip(other).all { (a, b) -> a == b })

internal fun ClauseQueue.orderedHashCode(): Int = fold(1) { hash, clause -> 31 * hash + clause.hashCode() }

/** Multisets are equal when they hold equal clauses with the same multiplicities. */
internal fun ClauseMultiSet.multiSetEquals(other: Any?): Boolean =
    this === other ||
        (other is ClauseMultiSet && size == other.size && counts() == other.counts())

internal fun ClauseMultiSet.counts(): Map<Clause, Int> = groupingBy { it }.eachCount()

internal fun ClauseMultiSet.multiSetHashCode(): Int = sumOf { it.hashCode() }
