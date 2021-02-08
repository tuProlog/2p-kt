package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import it.unibo.tuprolog.utils.setTags
import it.unibo.tuprolog.core.ListIterator as LogicListIterator

internal open class ConsImpl(
    override val head: Term,
    override val tail: Term,
    tags: Map<String, Any> = emptyMap()
) : CollectionImpl(Cons.FUNCTOR, arrayOf(head, tail), tags), Cons {

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { LogicListIterator.All(this) }.asSequence()

    override val functor: String = Cons.FUNCTOR

    override val args: Array<Term> get() = super<CollectionImpl>.args

    override val isWellFormed: Boolean by lazy { last is EmptyList }

    override fun toArray(): Array<Term> =
        when {
            isWellFormed -> unfoldedArray.sliceArray(0 until unfoldedArray.lastIndex)
            else -> unfoldedArray
        }

    override fun toList(): List<Term> =
        when {
            isWellFormed -> unfoldedList.subList(0, unfoldedList.lastIndex)
            else -> unfoldedList
        }

    override fun toSequence(): Sequence<Term> = LogicListIterator.SkippingLast(this).asSequence()

    override val last: Term by lazy { unfoldedSequence.last() }

    override fun toString(): String {
        val (ending, take) = if (isWellFormed) {
            "]" to size
        } else {
            " | $last]" to size - 1
        }
        return unfoldedSequence.take(take).joinToString(", ", "[", ending)
    }

    override fun apply(substitution: Substitution): Term {
        return when {
            substitution is Substitution.Fail -> throw SubstitutionApplicationException(this, substitution)
            substitution.isEmpty() -> this
            else -> ConsSubstitutionDecorator(this, substitution as Substitution.Unifier)
        }
    }

    override fun copyWithTags(tags: Map<String, Any>): Cons = ConsImpl(head, tail, tags)

    override fun freshCopy(): Cons = super.freshCopy() as Cons

    override fun freshCopy(scope: Scope): Cons = when {
        isGround -> this
        isWellFormed -> scope.listOf(toSequence().map { it.freshCopy(scope) }).setTags(tags) as Cons
        else -> scope.listFrom(
            unfoldedList.subList(0, unfoldedList.lastIndex).map { it.freshCopy(scope) },
            last = unfoldedList.last().freshCopy(scope)
        ).setTags(tags) as Cons
    }
}
