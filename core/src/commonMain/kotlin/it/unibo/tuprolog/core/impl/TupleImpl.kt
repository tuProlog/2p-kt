package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.TupleIterator

internal class TupleImpl(override val left: Term, override val right: Term) :
    CollectionImpl(Tuple.FUNCTOR, arrayOf(left, right)), Tuple {

    override val unfoldedSequence: Sequence<Term>
        get() = TupleIterator(this).asSequence()

    override val size: Int get() = unfoldedList.size

    override val functor: String = Tuple.FUNCTOR

    override val args: Array<Term> get() = super<CollectionImpl>.args

    override fun toString(): String = unfoldedSequence.joinToString(", ", "(", ")")
}