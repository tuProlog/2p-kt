package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Couple
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.List as LogicList

internal class CoupleImpl(override val head: Term, override val tail: Term) : StructImpl(Couple.FUNCTOR, arrayOf(head, tail)), Couple {

    override val unfoldedSequence: Sequence<Term> by lazy {
        sequenceOf(head) + if (tail.isList) tail.castTo<LogicList>().unfoldedSequence else sequenceOf(tail)
    }

    override val unfoldedList: List<Term> by lazy {
        unfoldedSequence.toList()
    }

    override val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override val functor: String = Couple.FUNCTOR

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toString(): String {
        val ending = if (unfoldedList.last() is EmptyList) {
            "]"
        } else {
            " | ${unfoldedList.last()}]"
        }
        return unfoldedSequence.take(unfoldedList.size - 1).joinToString(", ", "[", ending)
    }
}
