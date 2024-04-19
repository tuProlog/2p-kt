package it.unibo.tuprolog.core

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
        val TRUE: Truth = TermFactory.default.truthOf(true)

        @JvmField
        val FAIL: Truth = TermFactory.default.fail()

        @JvmField
        val FALSE: Truth = TermFactory.default.truthOf(false)

        @JvmStatic
        @JsName("of")
        fun of(value: Boolean): Truth = TermFactory.default.truthOf(value)

        @JvmStatic
        @JsName("ofString")
        fun of(value: String): Truth = TermFactory.default.truthOf(value)
    }
}
