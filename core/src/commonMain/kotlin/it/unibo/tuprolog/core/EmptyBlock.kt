package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

interface EmptyBlock : Empty, Block {
    override val isEmptyBlock: Boolean
        get() = true

    override fun freshCopy(): EmptyBlock

    override fun freshCopy(scope: Scope): EmptyBlock

    override fun asEmptyBlock(): EmptyBlock = this

    companion object {
        const val FUNCTOR: String = Terms.EMPTY_BLOCK_FUNCTOR

        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): EmptyBlock = TermFactory.default.emptyBlock()

        @JvmStatic
        @JsName("instance")
        @get:JvmName("instance")
        val instance: EmptyBlock
            get() = TermFactory.default.emptyBlock()
    }
}
