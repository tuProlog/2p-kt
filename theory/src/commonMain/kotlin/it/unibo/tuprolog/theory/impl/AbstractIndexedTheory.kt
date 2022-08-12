package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory

internal abstract class AbstractIndexedTheory protected constructor(
    queue: ClauseQueue,
    tags: Map<String, Any>
) : AbstractTheory(tags) {
    protected open val queue: ClauseQueue = queue

    override val clauses: Iterable<Clause> get() = Iterable { queue.iterator() }

    override fun get(clause: Clause): Sequence<Clause> = queue[clause]

    override val size: Long
        get() = queue.size.toLong()

    override val isEmpty: Boolean
        get() = queue.isEmpty()

    override val isNonEmpty: Boolean
        get() = !isEmpty
}
