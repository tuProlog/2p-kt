package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface Empty : Atom {

    override fun freshCopy(): Empty

    override fun freshCopy(scope: Scope): Empty

    companion object {

        const val EMPTY_LIST_FUNCTOR = "[]"

        const val EMPTY_SET_FUNCTOR = Set.FUNCTOR

        @JvmStatic
        @JsName("list")
        fun list(): EmptyList = EmptyList()

        @JvmStatic
        @JsName("set")
        fun set(): EmptySet = EmptySet()
    }
}
