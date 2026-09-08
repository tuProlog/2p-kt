package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.CONS_FUNCTOR
import it.unibo.tuprolog.core.impl.ConsImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

/**
 * A non-empty logic [LogicList], i.e. a [Struct] with functor `.` and [arity] `2`, conventionally read as
 * "[head] followed by [tail]". A well-formed [Cons] chain ends in [EmptyList]; when [tail] is anything else
 * (another [Var], or a non-list term), the list is a *partial* (a.k.a. improper) list — see
 * [List.isWellFormed].
 */
interface Cons : LogicList {
    override val isCons: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = false

    /** The first element of this list. */
    @JsName("head")
    val head: Term

    /** The rest of this list: either another [Cons], an [EmptyList], or an arbitrary [Term] for a partial list. */
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
        /** The canonical list-cell functor: `.` */
        const val FUNCTOR = CONS_FUNCTOR

        /** Creates a [Cons] with [head] as first element and [tail] as the rest of the list. */
        @JvmStatic
        @JsName("of")
        fun of(
            head: Term,
            tail: Term,
        ): Cons = ConsImpl(head, tail)

        /** Creates a one-element, well-formed logic list containing only [head]. */
        @JvmStatic
        @JsName("singleton")
        fun singleton(head: Term): Cons = of(head, Empty.list())
    }
}
