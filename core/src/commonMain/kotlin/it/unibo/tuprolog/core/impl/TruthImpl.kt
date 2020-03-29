package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Truth

internal class TruthImpl(value: String, override val isTrue: Boolean) : Truth, AtomImpl(value) {
    companion object {
        val TRUE = TruthImpl(Truth.TRUE_FUNCTOR, true)
        val FAIL = TruthImpl(Truth.FAIL_FUNCTOR, false)
        val FALSE = TruthImpl(Truth.FALSE_FUNCTOR, false)
    }
}
