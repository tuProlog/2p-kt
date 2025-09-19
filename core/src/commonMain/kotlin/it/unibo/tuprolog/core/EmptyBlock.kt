package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyBlockImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface EmptyBlock :
    Empty,
    Block {
    override val isEmptyBlock: Boolean
        get() = true

    override fun freshCopy(): EmptyBlock

    override fun freshCopy(scope: Scope): EmptyBlock

    override fun asEmptyBlock(): EmptyBlock = this

    companion object {
        const val FUNCTOR: String = Terms.EMPTY_BLOCK_FUNCTOR

        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): EmptyBlock = EmptyBlockImpl()

        @JvmStatic
        @JsName("instance")
        val instance: EmptyBlock = EmptyBlockImpl()
    }
}
