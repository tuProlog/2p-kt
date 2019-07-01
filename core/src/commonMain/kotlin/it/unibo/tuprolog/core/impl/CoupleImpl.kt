package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Couple
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.List as LogicList

internal class CoupleImpl(override val head: Term, override val tail: Term) : StructImpl(Couple.FUNCTOR, arrayOf(head, tail)), Couple {

    private val unfoldedSequence: Sequence<Term> by lazy {
        sequenceOf(head) + if (tail.isList) tail.castTo<LogicList>().toSequence() else sequenceOf(tail)
    }

    private val unfoldedList: List<Term> by lazy {
        unfoldedSequence.toList()
    }

    private val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override val functor: String = Couple.FUNCTOR

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override fun toList(): List<Term> = unfoldedList

    override fun toString(): String = unfoldedList.joinToString(", ", "[", "]")
}
