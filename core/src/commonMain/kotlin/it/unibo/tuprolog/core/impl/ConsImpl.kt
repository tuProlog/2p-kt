package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.List as LogicList

internal class ConsImpl(override val head: Term, override val tail: Term) :
    StructImpl(Cons.FUNCTOR, arrayOf(head, tail)), Cons {

    override val unfoldedSequence: Sequence<Term> by lazy {
        sequenceOf(head) + if (tail.isList) tail.`as`<LogicList>().unfoldedSequence else sequenceOf(tail)
    }

    override val unfoldedList: List<Term> by lazy {
        unfoldedSequence.toList()
    }

    override val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override val functor: String = Cons.FUNCTOR

    override val args: Array<Term> by lazy { super<StructImpl>.args }

    override val isWellFormed: Boolean by lazy {
        tail is LogicList && tail.isWellFormed
    }

    override fun toString(): String {
        val ending = if (isWellFormed) {
            "]"
        } else {
            " | ${unfoldedList.last()}]"
        }
        return unfoldedSequence.take(unfoldedList.size - 1).joinToString(", ", "[", ending)
    }
}
