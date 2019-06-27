package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyListImpl
import it.unibo.tuprolog.core.List as PrologList

interface EmptyList : Empty, PrologList {

    override val isCouple: Boolean
        get() = false

    override val isList: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = true

    override fun toArray(): Array<Term> {
        return arrayOf()
    }

    override fun toList(): List<Term> {
        return emptyList()
    }

    override fun toSequence(): Sequence<Term> {
        return emptySequence()
    }

    companion object {
        operator fun invoke(): EmptyList {
            return EmptyListImpl
        }
    }
}