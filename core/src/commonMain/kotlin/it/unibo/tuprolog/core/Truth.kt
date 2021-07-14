package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Truth : Atom {

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

        @JvmField
        val TRUE_FUNCTOR = Terms.TRUE_FUNCTOR

        @JvmField
        val FALSE_FUNCTOR = Terms.FALSE_FUNCTOR

        @JvmField
        val FAIL_FUNCTOR = Terms.FAIL_FUNCTOR

        @JvmField
        val TRUE: Truth = TruthImpl(Terms.TRUE_FUNCTOR, true)

        @JvmField
        val FAIL: Truth = TruthImpl(Terms.FAIL_FUNCTOR, false)

        @JvmField
        val FALSE: Truth = TruthImpl(Terms.FALSE_FUNCTOR, false)

        @JvmStatic
        @JsName("of")
        fun of(truth: Boolean): Truth =
            if (truth) TRUE else FALSE

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
