package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ListIterator as LogicListIterator

internal class ConsImpl(override val head: Term, override val tail: Term) :
    CollectionImpl(Cons.FUNCTOR, arrayOf(head, tail)), Cons {

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { LogicListIterator.All(this) }.asSequence()

    override val functor: String = Cons.FUNCTOR

    override val args: Array<Term> get() = super<CollectionImpl>.args

    override val isWellFormed: Boolean by lazy {
        last is EmptyList
    }

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

    override val last: Term by lazy {
        unfoldedSequence.last()
    }

    override fun unfold(): Sequence<Term> =
        Iterable { ListUnfolder(this) }.asSequence()

    override fun toString(): String {
        val (ending, take) = if (isWellFormed) {
            "]" to size
        } else {
            " | $last]" to size - 1
        }
        return unfoldedSequence.take(take).joinToString(", ", "[", ending)
    }
}
