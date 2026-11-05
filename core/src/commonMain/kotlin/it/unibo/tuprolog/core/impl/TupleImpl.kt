package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.TupleIterator
import it.unibo.tuprolog.utils.setTags

internal class TupleImpl(
    override val left: Term,
    override val right: Term,
    tags: Map<String, Any> = emptyMap(),
) : RecursiveImpl(TUPLE_FUNCTOR, listOf(left, right), tags),
    Tuple {
    private val eagerGround: Boolean? = knownGroundOf(left, right)

    private val eagerSize: Int? = if (right.isTuple) right.knownSize?.plus(1) else 2

    override val isGround: Boolean
        get() = eagerGround ?: super<RecursiveImpl>.isGround

    override val knownGround: Boolean?
        get() = eagerGround ?: super.knownGround

    override val size: Int
        get() = eagerSize ?: super<RecursiveImpl>.size

    override val knownSize: Int?
        get() = eagerSize ?: super.knownSize

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { TupleIterator(this) }.asSequence()


    override fun unfold(): Sequence<Term> = Iterable { TupleUnfolder(this) }.asSequence()

    override val functor: String = TUPLE_FUNCTOR

    override fun toString(): String = unfoldedSequence.joinToString(", ", "(", ")")

    override fun copyWithTags(tags: Map<String, Any>): Tuple = TupleImpl(left, right, tags)

    override fun freshCopy(): Tuple = super.freshCopy().castToTuple()

    override fun freshCopy(scope: Scope): Tuple =
        when {
            isGround -> this
            else -> scope.tupleOf(unfoldedSequence.map { it.freshCopy(scope) }).setTags(tags)
        }

    /**
     * Applies [unifier] to every item, iteratively, rebuilding the tuple from its end: the longest suffix
     * left unchanged by [unifier] is reused as is (as is the whole tuple, if nothing changes).
     */
    override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term {
        val nodes = mutableListOf<Tuple>()
        var current: Term = this
        while (current is Tuple) {
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

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTuple(this)
}
