package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real

actual class AnyToTermConverterImpl actual constructor(override val prologScope: PrologScope) : AnyToTermConverter {

    companion object {
        private val INT_REGEX = "[0-9]+".toRegex()
    }

    private lateinit var numberCache: String

    override val Number.isInteger: Boolean
        get() {
            numberCache = this.toString()
            return INT_REGEX.matches(numberCache)
        }

    override fun Number.toInteger(): Integer {
        return Integer.of(numberCache)
    }

    override fun Number.toReal(): Real {
        return Real.of(numberCache)
    }
}