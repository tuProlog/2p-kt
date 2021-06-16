package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Terms.CONS_FUNCTOR
import it.unibo.tuprolog.utils.setTags
import it.unibo.tuprolog.core.ListIterator as LogicListIterator

internal abstract class AbstractCons(
    args: Array<Term>,
    tags: Map<String, Any> = emptyMap()
) : CollectionImpl(CONS_FUNCTOR, args, tags), Cons {

    companion object {
        const val SWITCH_TO_LAZY_THRESHOLD = 100
    }

    internal open val weight: Int
        get() = 1

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { LogicListIterator.All(this) }.asSequence()

    override val functor: String = CONS_FUNCTOR

    override val args: Array<Term> get() = super<CollectionImpl>.args

    override val isWellFormed: Boolean by lazy { last is EmptyList }

    override fun unfold(): Sequence<Term> = Iterable { ListUnfolder(this) }.asSequence()

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

    override fun isUnifierSkippable(unifier: Substitution.Unifier): Boolean = unifier.isEmpty()

    abstract override fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term

    abstract override fun copyWithTags(tags: Map<String, Any>): AbstractCons

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
