package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Collection
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.itemWiseHashCode

internal abstract class CollectionImpl(functor: String, args: Array<Term>) : StructImpl(functor, args), Collection {
    override val isGround: Boolean by lazy {
        unfoldedSequence.all { it.isGround }
    }

    override fun argsHashCode(): Int {
        return itemWiseHashCode(unfoldedSequence)
    }
}