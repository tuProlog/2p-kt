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
    private val eagerGround: Boolean? = knownGroundOf(left, right)

    private val eagerSize: Int? = if (right.isTuple) right.knownSize?.plus(1) else 2

    override val isGround: Boolean
        get() = eagerGround ?: super<AbstractTuple>.isGround

    override val knownGround: Boolean?
        get() = eagerGround ?: super.knownGround

    override val size: Int
        get() = eagerSize ?: super<AbstractTuple>.size

    override val knownSize: Int?
        get() = eagerSize ?: super.knownSize

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
