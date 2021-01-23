package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Truth : Atom {

    override val isTrue: Boolean

    override val isFail: Boolean
        get() = !isTrue

    override fun freshCopy(): Truth

    override fun freshCopy(scope: Scope): Truth

    @Suppress("MayBeConstant")
    companion object {

        @JvmField
        val TRUE_FUNCTOR = "true"

        @JvmField
        val FALSE_FUNCTOR = "false"

        @JvmField
        val FAIL_FUNCTOR = "fail"

        @JvmField
        val TRUE: Truth = TruthImpl(TRUE_FUNCTOR, true)

        @JvmField
        val FAIL: Truth = TruthImpl(FAIL_FUNCTOR, false)

        @JvmField
        val FALSE: Truth = TruthImpl(FALSE_FUNCTOR, false)

        @JvmStatic
        @JsName("of")
        fun of(truth: Boolean): Truth =
            if (truth) TRUE else FALSE

        @JvmStatic
        @JsName("ofString")
        fun of(string: String): Truth =
            when (string) {
                TRUE_FUNCTOR -> TRUE
                FALSE_FUNCTOR -> FALSE
                FAIL_FUNCTOR -> FAIL
                else -> throw IllegalArgumentException("Cannot parse $string as a Truth value")
            }
    }
}
