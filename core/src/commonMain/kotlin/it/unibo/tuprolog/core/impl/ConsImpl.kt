package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.core.ListIterator as LogicListIterator
import it.unibo.tuprolog.core.List as LogicList

internal class ConsImpl(override val head: Term, override val tail: Term) :
    CollectionImpl(Cons.FUNCTOR, arrayOf(head, tail)), Cons {

    override val unfoldedSequence: Sequence<Term>
        get() = LogicListIterator.All(this).asSequence()

    override val unfoldedList: List<Term> by lazy {
        dequeOf(unfoldedSequence)
    }

    override val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

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

    override fun toString(): String {
        val (ending, take) = if (isWellFormed) {
            "]" to size
        } else {
            " | ${last}]" to size - 1
        }
        return unfoldedSequence.take(take).joinToString(", ", "[", ending)
    }
}
