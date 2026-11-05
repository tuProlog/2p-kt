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
    // Metadata is propagated from the tail in O(1) when the tail already knows it, and otherwise computed
    // on demand (iteratively), so that consing onto a lazy list never traverses it.
    init {
        cachedGround = knownGroundOf(head, tail)
        cachedLast = if (tail.isCons) (tail as? AbstractCons)?.knownLast else tail
        cachedSize =
            when {
                tail.isCons -> successorOfKnownSize(tail.knownSize)
                tail.isEmptyList -> 1
                else -> 2
            }
    }

    override val estimatedLength: Int
        get() = if (knownSize >= 0) knownSize else 1

    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        if (estimatedLength >= SWITCH_TO_LAZY_THRESHOLD) {
            LazyConsWithImplicitLast(unfoldedSequence.cursor().map { it.apply(unifier) }, tags)
        } else {
            val newHead = head.apply(unifier)
            val newTail = tail.apply(unifier)
            if (newHead === head && newTail === tail) this else ConsImpl(newHead, newTail, tags)
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
