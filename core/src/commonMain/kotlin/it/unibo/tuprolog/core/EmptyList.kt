package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyListImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

/**
 * The empty logic list, i.e. the atom `[]`. It is a singleton: [EmptyList.instance] (equivalently,
 * `EmptyList()`) always returns the same instance, and [Atom.of] and [List.empty] both return it whenever
 * asked to build the `[]` atom.
 */
interface EmptyList :
    Empty,
    LogicList {
    override val isCons: Boolean
        get() = false

    override val isList: Boolean
        get() = true

    override val isEmptyList: Boolean
        get() = true

    override val isWellFormed: Boolean
        get() = true

    override fun toArray(): Array<Term> = arrayOf()

    override fun toList(): List<Term> = emptyList()

    override fun toSequence(): Sequence<Term> = emptySequence()

    override fun freshCopy(): EmptyList

    override fun freshCopy(scope: Scope): EmptyList

    override fun asEmptyList(): EmptyList = this

    companion object {
        /** The canonical empty-list functor: `[]` */
        const val FUNCTOR: String = Terms.EMPTY_LIST_FUNCTOR

        /** Returns the singleton [EmptyList] instance. Same as [instance]. */
        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): EmptyList = EmptyListImpl()

        /** The singleton [EmptyList] instance. */
        @JvmStatic
        @JsName("instance")
        val instance: EmptyList = EmptyListImpl()
    }
}
