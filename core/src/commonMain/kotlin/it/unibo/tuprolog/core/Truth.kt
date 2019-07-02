package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.TruthImpl

interface Truth : Atom {

    override val isTrue: Boolean
        get() = TRUE_FUNCTOR == functor

    override val isFail: Boolean
        get() = FAIL_FUNCTOR == functor

    override fun freshCopy(): Truth = this

    override fun freshCopy(scope: Scope): Truth = this

    companion object {
        const val TRUE_FUNCTOR = "true"
        const val FAIL_FUNCTOR = "fail"

        fun of(truth: Boolean): Truth =
                if (truth) TruthImpl.True else TruthImpl.Fail

        fun `true`(): Truth = TruthImpl.True

        fun fail(): Truth = TruthImpl.Fail
    }
}
