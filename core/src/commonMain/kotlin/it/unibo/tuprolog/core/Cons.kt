package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ConsImpl
import it.unibo.tuprolog.core.List as LogicList

interface Cons : Struct, LogicList {

    override val isCons: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = false

    val head: Term

    val tail: Term

    override val functor: String
        get() = FUNCTOR

    override val args: Array<Term>
        get() = arrayOf(head, tail)

    override val arity: Int
        get() = 2

    override fun freshCopy(): Cons = super<LogicList>.freshCopy() as Cons

    override fun freshCopy(scope: Scope): Cons = super<LogicList>.freshCopy(scope) as Cons

    companion object {
        const val FUNCTOR = "."

        fun of(head: Term, tail: Term): Cons = ConsImpl(head, tail)

        fun last(head: Term): Cons = of(head, Empty.list())
    }
}
