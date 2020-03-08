package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real

actual class AnyToTermConverterImpl actual constructor(override val prolog: Prolog) : AnyToTermConverter {
    override val Number.isInteger: Boolean
        get() = this is Int || this is Long

    override fun Number.toInteger(): Integer {
        return prolog.numOf(this.toLong())
    }

    override fun Number.toReal(): Real {
        return Real.of(this.toString())
    }
}