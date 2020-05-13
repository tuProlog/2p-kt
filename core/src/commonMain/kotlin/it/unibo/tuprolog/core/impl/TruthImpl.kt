package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Truth

internal class TruthImpl(value: String, override val isTrue: Boolean) : Truth, AtomImpl(value) {

    override fun toString(): String = value
}
