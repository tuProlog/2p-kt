package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.CONS_FUNCTOR
import it.unibo.tuprolog.core.impl.ConsImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

interface Cons : LogicList {
    override val isCons: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = false

    @JsName("head")
    val head: Term

    @JsName("tail")
    val tail: Term

    override val functor: String
        get() = CONS_FUNCTOR

    override val arity: Int
        get() = 2

    override fun freshCopy(): Cons

    override fun freshCopy(scope: Scope): Cons

    override fun asCons(): Cons = this

    companion object {
        const val FUNCTOR = CONS_FUNCTOR

        @JvmStatic
        @JsName("of")
        fun of(
            head: Term,
            tail: Term,
        ): Cons = ConsImpl(head, tail)

        @JvmStatic
        @JsName("singleton")
        fun singleton(head: Term): Cons = of(head, Empty.list())
    }
}
