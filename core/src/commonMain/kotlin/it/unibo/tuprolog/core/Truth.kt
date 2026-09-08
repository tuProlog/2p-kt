package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * An [Atom] representing one of Prolog's canonical boolean values: `true`, `fail`, or `false`.
 *
 * [Truth] exists mostly so that resolution/solving code (in downstream modules) can recognize and construct
 * these three atoms without comparing raw strings; [TRUE], [FAIL], and [FALSE] are the only three [Truth]
 * instances, and [Atom.of] automatically returns one of them whenever its argument matches one of the three
 * corresponding functors, so most client code never needs to reach for [Truth] directly.
 */
interface Truth : Atom {
    /** `true` for [TRUE], `false` for both [FAIL] and [FALSE]. */
    override val isTrue: Boolean

    override val isFail: Boolean
        get() = !isTrue

    override val isTruth: Boolean
        get() = true

    override fun freshCopy(): Truth

    override fun freshCopy(scope: Scope): Truth

    override fun asTruth(): Truth = this

    @Suppress("MayBeConstant")
    companion object {
        /** The functor of [TRUE], i.e. `"true"`. */
        @JvmField
        val TRUE_FUNCTOR = Terms.TRUE_FUNCTOR

        /** The functor of [FALSE], i.e. `"false"`. */
        @JvmField
        val FALSE_FUNCTOR = Terms.FALSE_FUNCTOR

        /** The functor of [FAIL], i.e. `"fail"`. */
        @JvmField
        val FAIL_FUNCTOR = Terms.FAIL_FUNCTOR

        /** The `true` atom. */
        @JvmField
        val TRUE: Truth = TruthImpl(Terms.TRUE_FUNCTOR, true)

        /** The `fail` atom. */
        @JvmField
        val FAIL: Truth = TruthImpl(Terms.FAIL_FUNCTOR, false)

        /** The `false` atom. */
        @JvmField
        val FALSE: Truth = TruthImpl(Terms.FALSE_FUNCTOR, false)

        /** Returns [TRUE] if [truth] is `true`, or [FALSE] otherwise. */
        @JvmStatic
        @JsName("of")
        fun of(truth: Boolean): Truth = if (truth) TRUE else FALSE

        /**
         * Returns the [Truth] whose functor equals [string].
         * @throws IllegalArgumentException if [string] is none of `"true"`, `"false"`, or `"fail"`
         */
        @JvmStatic
        @JsName("ofString")
        fun of(string: String): Truth =
            when (string) {
                Terms.TRUE_FUNCTOR -> TRUE
                Terms.FALSE_FUNCTOR -> FALSE
                Terms.FAIL_FUNCTOR -> FAIL
                else -> throw IllegalArgumentException("Cannot parse $string as a Truth value")
            }
    }
}
