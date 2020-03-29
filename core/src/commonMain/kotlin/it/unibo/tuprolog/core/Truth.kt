package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Truth : Atom {

    override val isTrue: Boolean

    override val isFail: Boolean
        get() = !isTrue

    override fun freshCopy(): Truth = this

    override fun freshCopy(scope: Scope): Truth = this

    companion object {

        @JvmField
        val TRUE_FUNCTOR = "true"
        @JvmField
        val FALSE_FUNCTOR = "false"
        @JvmField
        val FAIL_FUNCTOR = "fail"

        @JvmField
        val TRUE: Truth = TruthImpl.True

        @JvmField
        val FAIL: Truth = TruthImpl.Fail

        @JvmField
        val FALSE: Truth = TruthImpl.False

        @JvmStatic
        fun of(truth: Boolean): Truth =
            if (truth) TRUE else FALSE

        @JvmStatic
        fun of(string: String): Truth =
            when (string) {
                TRUE_FUNCTOR -> TRUE
                FALSE_FUNCTOR -> FALSE
                FAIL_FUNCTOR -> FAIL
                else -> throw IllegalArgumentException("Cannot parse $string as a Truth value")
            }
    }
}
