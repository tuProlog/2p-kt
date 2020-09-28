package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory

internal abstract class AbstractIndexedTheory protected constructor(queue: ClauseQueue) : AbstractTheory() {
    protected open val queue: ClauseQueue = queue

    override val clauses: Iterable<Clause> by lazy { queue.toList() }

    override fun get(clause: Clause): Sequence<Clause> = queue[clause]
}
