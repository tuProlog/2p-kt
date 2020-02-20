package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Truth : Atom {

    override val isTrue: Boolean
        get() = TRUE_FUNCTOR == functor

    override val isFail: Boolean
        get() = FAIL_FUNCTOR == functor

    override fun freshCopy(): Truth = this

    override fun freshCopy(scope: Scope): Truth = this

    companion object {

        @JvmField
        val TRUE_FUNCTOR = "true"

        @JvmField
        val FAIL_FUNCTOR = "fail"

        @JvmField
        val TRUE = ofTrue()

        @JvmField
        val FAIL = ofFalse()

        @JvmStatic
        fun of(truth: Boolean): Truth =
            if (truth) TruthImpl.True else TruthImpl.Fail

        @JvmStatic
        fun ofTrue(): Truth = TruthImpl.True

        @JvmStatic
        fun ofFalse(): Truth = TruthImpl.Fail
    }
}
