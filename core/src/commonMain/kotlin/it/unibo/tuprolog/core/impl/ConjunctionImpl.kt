package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Conjunction
import it.unibo.tuprolog.core.Term

internal class ConjunctionImpl(override val left: Term, override val right: Term) : StructImpl(Conjunction.FUNCTOR, arrayOf(left, right)), Conjunction {

    override val unfoldedSequence: Sequence<Term> by lazy {
        sequenceOf(left) + if (right is Conjunction) right.unfoldedSequence else sequenceOf(right)
    }

    override val unfoldedList: List<Term> by lazy {
        unfoldedSequence.toList()
    }

    override val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override val functor: String = Conjunction.FUNCTOR

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toString(): String {
        return unfoldedSequence.joinToString(", ", "(", ")")
    }
}