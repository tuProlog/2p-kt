package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.cursor
import it.unibo.tuprolog.utils.setTags

internal class ConsImpl(
    override val head: Term,
    override val tail: Term,
    tags: Map<String, Any> = emptyMap(),
) : AbstractCons(listOf(head, tail), tags),
    Cons {
    override val isGround: Boolean = checkGroundness()

    override val last: Term =
        when {
            tail.isList -> tail.castToList().last
            else -> tail
        }

    override val size: Int =
        when {
            tail.isList -> 1 + tail.castToList().size
            else -> 2
        }

    override val isWellFormed: Boolean =
        when {
            tail.isList -> tail.castToList().isWellFormed
            else -> false
        }

    override fun checkGroundness(): Boolean = head.isGround && tail.isGround

    override val estimatedLength: Int
        get() = size

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        if (estimatedLength >= SWITCH_TO_LAZY_THRESHOLD) {
            LazyConsWithImplicitLast(unfoldedSequence.cursor().map { it.apply(unifier) }, tags)
        } else {
            ConsImpl(head.apply(unifier), tail.apply(unifier), tags)
        }

    override fun copyWithTags(tags: Map<String, Any>): ConsImpl =
        if (this.tags === tags) {
            this
        } else {
            ConsImpl(
                head,
                tail,
                tags,
            )
        }

    override fun freshCopy(scope: Scope): Cons =
        when {
            isGround -> this
            isWellFormed -> scope.logicListOf(toList().map { it.freshCopy(scope) }).setTags(tags).castToCons()
            else -> scope.logicListFrom(unfoldedList.map { it.freshCopy(scope) }).setTags(tags).castToCons()
        }
}
