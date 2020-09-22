package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.SetIterator
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Set as LogicSet

internal open class SetImpl(private val item: Term?) :
    CollectionImpl(LogicSet.FUNCTOR, listOfNotNull(item).toTypedArray()), LogicSet {

    override val functor: String = super<LogicSet>.functor

    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { SetIterator(this) }.asSequence()

    override val size: Int get() = unfoldedList.size

    override fun unfold(): Sequence<Term> =
        Iterable { SetUnfolder(this) }.asSequence()

    override fun toString(): String = unfoldedSequence.joinToString(", ", "{", "}")
}
