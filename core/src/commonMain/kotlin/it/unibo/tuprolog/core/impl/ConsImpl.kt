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
    private val eagerGround: Boolean? = knownGroundOf(head, tail)

    private val eagerLast: Term? = if (tail.isCons) (tail as? AbstractCons)?.knownLast else tail

    private val eagerSize: Int? =
        when {
            tail.isCons -> tail.knownSize?.plus(1)
            tail.isEmptyList -> 1
            else -> 2
        }

    override val isGround: Boolean
        get() = eagerGround ?: super<AbstractCons>.isGround

    override val knownGround: Boolean?
        get() = eagerGround ?: super.knownGround

    override val last: Term
        get() = eagerLast ?: super<AbstractCons>.last

    override val knownLast: Term?
        get() = eagerLast ?: super.knownLast

    override val size: Int
        get() = eagerSize ?: super<AbstractCons>.size

    override val knownSize: Int?
        get() = eagerSize ?: super.knownSize

    override val estimatedLength: Int
        get() = eagerSize ?: 1

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
