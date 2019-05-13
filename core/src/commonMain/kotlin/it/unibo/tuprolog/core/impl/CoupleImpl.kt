package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Couple
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Term

internal class CoupleImpl(override val head: Term, override val tail: Term) : StructImpl(Couple.FUNCTOR, arrayOf(head, tail)), Couple {

    private val unfoldedSequence: Sequence<Term> by lazy {
        sequenceOf(head) + if (tail.isList) tail.castTo<List>().toSequence() else sequenceOf(tail)
    }

    private val unfoldedList: kotlin.collections.List<Term> by lazy {
        unfoldedSequence.toList()
    }

    private val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override val functor: String = Couple.FUNCTOR

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toArray(): Array<Term> {
        return unfoldedArray
    }

    override fun toSequence(): Sequence<Term> {
        return unfoldedSequence
    }

    override fun toList(): kotlin.collections.List<Term> {
        return unfoldedList
    }

    override fun toString(): String {
        return unfoldedList.joinToString(", ", "[", "]")
    }


}