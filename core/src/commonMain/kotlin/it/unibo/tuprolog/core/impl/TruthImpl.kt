package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Truth

internal class TruthImpl(private val truth: Boolean) : AtomImpl(if (truth) Truth.TRUE_FUNCTOR else Truth.FAIL_FUNCTOR), Truth {
    override val isTrue: Boolean = truth
    override val isFail: Boolean = !truth

    companion object {
        val True = TruthImpl(true)
        val Fail = TruthImpl(false)
    }
}