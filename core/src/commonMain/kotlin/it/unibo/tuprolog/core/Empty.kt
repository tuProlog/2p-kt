package it.unibo.tuprolog.core

import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Base type for the two "empty" singleton atoms of the term hierarchy: [EmptyList] (`[]`) and [EmptyBlock]
 * (`{}`). Both are 0-arity [Atom]s that also happen to be recognized as (degenerate) [Recursive] structures
 * with zero elements.
 */
interface Empty : Atom {
    override fun freshCopy(): Empty

    override fun freshCopy(scope: Scope): Empty

    companion object {
        /** The canonical empty-list functor: `[]` (same as [EmptyList.FUNCTOR]). */
        const val EMPTY_LIST_FUNCTOR = Terms.EMPTY_LIST_FUNCTOR

        /** The canonical empty-block functor: `{}` (same as [EmptyBlock.FUNCTOR]). */
        const val EMPTY_BLOCK_FUNCTOR = Terms.EMPTY_BLOCK_FUNCTOR

        /** Returns the singleton empty logic list, i.e. the atom `[]`. */
        @JvmStatic
        @JsName("list")
        fun list(): EmptyList = EmptyList()

        /** Returns the singleton empty logic block, i.e. the atom `{}`. */
        @JvmStatic
        @JsName("block")
        fun block(): EmptyBlock = EmptyBlock()
    }
}
