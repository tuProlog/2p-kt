package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.unify.Unificator

internal abstract class AbstractIndexedTheory protected constructor(
    queue: ClauseQueue,
    tags: Map<String, Any>
) : AbstractTheory(tags) {

    @Suppress("UNUSED_PARAMETER")
    override var unificator: Unificator
        get() = queue.unificator
        set(value) {
            error("Indexed theories do not support changing the unification without rebuilding")
        }

    protected open val queue: ClauseQueue = queue

    override val directives: Iterable<Directive>
        get() = queue.directives

    override val rules: Iterable<Rule>
        get() = queue.rules

    override val clauses: Iterable<Clause>
        get() = Iterable { queue.iterator() }

    override fun get(clause: Clause): Sequence<Clause> = queue[clause]

    override val size: Long
        get() = queue.size.toLong()

    override val isEmpty: Boolean
        get() = queue.isEmpty()

    override val isNonEmpty: Boolean
        get() = !isEmpty
}
