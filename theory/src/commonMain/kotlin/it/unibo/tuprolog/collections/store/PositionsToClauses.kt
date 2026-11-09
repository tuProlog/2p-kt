package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause

internal class PositionsToClauses(
    private val positions: IntIterator,
    private val clauses: Array<Clause>,
) : Iterator<Clause> {
    override fun hasNext(): Boolean = positions.hasNext()

    override fun next(): Clause = clauses[positions.nextInt()]
}
