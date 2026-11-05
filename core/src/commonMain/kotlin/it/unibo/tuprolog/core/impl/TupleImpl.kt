package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
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
            else -> scope.tupleOf(argsSequence.map { it.freshCopy(scope) }).setTags(tags)
        }

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTuple(this)
}
