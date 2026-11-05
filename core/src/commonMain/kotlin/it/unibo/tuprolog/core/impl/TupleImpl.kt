package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.utils.setTags

internal class TupleImpl(
    override val left: Term,
    override val right: Term,
    tags: Map<String, Any> = emptyMap(),
) : AbstractTuple(listOf(left, right), tags) {
    init {
        cachedGround = knownGroundOf(left, right)
        cachedSize = if (right.isTuple) successorOfKnownSize(right.knownSize) else 2
    }

    override fun copyWithTags(tags: Map<String, Any>): Tuple = TupleImpl(left, right, tags)

    override fun freshCopy(scope: Scope): Tuple =
        when {
            isGround -> this
            else -> Tuple.of(unfoldedSequence.map { it.freshCopy(scope) }.toList()).setTags(tags)
        }

    /**
     * Applies [unifier] to every item, iteratively, rebuilding the tuple from its end: the longest suffix
     * left unchanged by [unifier] is reused as is (as is the whole tuple, if nothing changes). A lazy
     * suffix, if any, is substituted lazily.
     */
    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term {
        val nodes = mutableListOf<TupleImpl>()
        var current: Term = this
        while (current is TupleImpl) {
            nodes.add(current)
            current = current.right
        }
        var result: Term = current.apply(unifier)
        var shared = result === current
        for (i in nodes.indices.reversed()) {
            val node = nodes[i]
            val left = node.left.apply(unifier)
            if (shared && left === node.left) {
                result = node
            } else {
                shared = false
                result = TupleImpl(left, result, node.tags)
            }
        }
        return result
    }
}
