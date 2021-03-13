package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptySetImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.Set as LogicSet

interface EmptySet : Empty, LogicSet {

    override val isEmptySet: Boolean
        get() = true

    override fun freshCopy(): EmptySet

    override fun freshCopy(scope: Scope): EmptySet

    override fun asEmptySet(): EmptySet = this

    companion object {
        const val FUNCTOR: String = Terms.EMPTY_SET_FUNCTOR

        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): EmptySet = EmptySetImpl()

        @JvmStatic
        @JsName("instance")
        val instance: EmptySet = EmptySetImpl()
    }
}
