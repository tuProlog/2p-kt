package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.EmptyBlockImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The empty logic block, i.e. the atom `{}`. It is a singleton: [EmptyBlock.instance] (equivalently,
 * `EmptyBlock()`) always returns the same instance, and [Atom.of] and [Block.empty] both return it whenever
 * asked to build the `{}` atom.
 */
interface EmptyBlock :
    Empty,
    Block {
    override val isEmptyBlock: Boolean
        get() = true

    override fun freshCopy(): EmptyBlock

    override fun freshCopy(scope: Scope): EmptyBlock

    override fun asEmptyBlock(): EmptyBlock = this

    companion object {
        /** The canonical empty-block functor: `{}` */
        const val FUNCTOR: String = Terms.EMPTY_BLOCK_FUNCTOR

        /** Returns the singleton [EmptyBlock] instance. Same as [instance]. */
        @JvmStatic
        @JsName("invoke")
        operator fun invoke(): EmptyBlock = EmptyBlockImpl()

        /** The singleton [EmptyBlock] instance. */
        @JvmStatic
        @JsName("instance")
        val instance: EmptyBlock = EmptyBlockImpl()
    }
}
