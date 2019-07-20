package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyListImpl
import it.unibo.tuprolog.core.List as LogicList

interface EmptyList : Empty, LogicList {

    override val isCons: Boolean
        get() = false

    override val isList: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = true

    override fun toArray(): Array<Term> = arrayOf()

    override fun toList(): List<Term> = emptyList()

    override fun toSequence(): Sequence<Term> = emptySequence()

    override fun freshCopy(): EmptyList = this

    override fun freshCopy(scope: Scope): EmptyList = this

    override fun <T> accept(visitor: TermVisitor<T>): T =
            visitor.visit(this)

    companion object {
        operator fun invoke(): EmptyList = EmptyListImpl
    }
}
