package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Conjunction
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Set as LogicSet

internal open class SetImpl(private val item: Term?) : StructImpl(LogicSet.FUNCTOR, if (item == null) emptyArray() else arrayOf(item)), LogicSet {

    override val functor: String
        get() = super<LogicSet>.functor

    override val unfoldedSequence: Sequence<Term> by lazy {
        when (item) {
            null -> emptySequence()
            is Conjunction -> item.unfoldedSequence
            else -> sequenceOf(item)
        }
    }

    override val unfoldedList: List<Term> by lazy {
        unfoldedSequence.toList()
    }

    override val unfoldedArray: Array<Term> by lazy {
        unfoldedList.toTypedArray()
    }

    override fun toString(): String = unfoldedSequence.joinToString(", ", "{", "}")
}