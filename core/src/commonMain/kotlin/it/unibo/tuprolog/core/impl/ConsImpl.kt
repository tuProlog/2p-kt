package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.cursor

internal open class ConsImpl(
    override val head: Term,
    override val tail: Term,
    tags: Map<String, Any> = emptyMap()
) : AbstractCons(arrayOf(head, tail), tags), Cons {
    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        LazyConsWithImplicitLast(unfoldedSequence.cursor().map { it.apply(unifier) }, tags)

    override fun copyWithTags(tags: Map<String, Any>): ConsImpl =
        if (this.tags === tags) this else ConsImpl(head, tail, tags)
}
