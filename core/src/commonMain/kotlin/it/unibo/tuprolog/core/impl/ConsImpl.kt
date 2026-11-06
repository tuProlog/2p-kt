package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
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

    /**
     * Applies [unifier] to every item: the longest suffix left unchanged by [unifier] is reused as is (as is the
     * whole list, if nothing changes), ground suffixes are not even visited, and a lazy suffix, if any, is
     * substituted lazily. Short lists recur on their tail, longer ones (or ones of unknown size) are rebuilt
     * iteratively from their end.
     */
    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term =
        if (isRecursivelyTransformable(knownSize)) {
            val newHead = head.apply(unifier)
            val newTail = tail.apply(unifier)
            if (newHead === head && newTail === tail) this else ConsImpl(newHead, newTail, tags)
        } else {
            applyIteratively(unifier)
        }

    private fun applyIteratively(unifier: Substitution.Unifier): Term {
        val nodes = mutableListOf<ConsImpl>()
        var current: Term = this
        while (current is ConsImpl && current.knownGround != true) {
            nodes.add(current)
            current = current.tail
        }
        var result: Term = current.apply(unifier)
        var shared = result === current
        for (i in nodes.indices.reversed()) {
            val node = nodes[i]
            val head = node.head.apply(unifier)
            if (shared && head === node.head) {
                result = node
            } else {
                shared = false
                result = ConsImpl(head, result, node.tags)
            }
        }
        return result
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
