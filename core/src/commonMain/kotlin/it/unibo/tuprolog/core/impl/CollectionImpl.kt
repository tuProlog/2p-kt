package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.itemWiseHashCode

internal abstract class CollectionImpl(functor: String, args: Array<Term>) : StructImpl(functor, args), Collection {

    override val unfoldedList: List<Term> by lazy {
        dequeOf(unfoldedSequence)
    }

    override val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override val isGround: Boolean by lazy {
        unfoldedSequence.all { it.isGround }
    }

    override fun argsHashCode(): Int {
        return itemWiseHashCode(unfoldedSequence)
    }

    override val variables: Sequence<Var> by lazy {
        unfoldedSequence.flatMap { it.variables }
    }
}
