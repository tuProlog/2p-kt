package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ConsImpl
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

interface Cons : LogicList {

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

    override fun freshCopy(): Cons = super.freshCopy() as Cons

    override fun freshCopy(scope: Scope): Cons = super.freshCopy(scope) as Cons

    companion object {

        const val FUNCTOR = "."

        @JvmStatic
        fun of(head: Term, tail: Term): Cons = ConsImpl(head, tail)

        @JvmStatic
        fun singleton(head: Term): Cons = of(head, Empty.list())
    }
}
