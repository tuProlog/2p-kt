package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Truth

internal sealed class TruthImpl(value: String, override val isTrue: Boolean) : Truth, AtomImpl(value) {
    object True : TruthImpl(Truth.TRUE_FUNCTOR, true) {
        override fun toString(): String = value
    }
    object Fail : TruthImpl(Truth.FAIL_FUNCTOR, false) {
        override fun toString(): String = value
    }
    object False : TruthImpl(Truth.FALSE_FUNCTOR, false) {
        override fun toString(): String = value
    }
}
