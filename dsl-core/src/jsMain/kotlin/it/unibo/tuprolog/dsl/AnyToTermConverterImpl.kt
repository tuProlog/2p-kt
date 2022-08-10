package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.utils.NumberTypeTester

actual class AnyToTermConverterImpl actual constructor(override val prologScope: LogicProgrammingScope) : AnyToTermConverter {

    private val numberTypeTester = NumberTypeTester()

    override val Number.isInteger: Boolean
        get() = numberTypeTester.numberIsInteger(this)

    override fun Number.toInteger(): Integer {
        return Integer.of(numberTypeTester.numberToInteger(this))
    }

    override fun Number.toReal(): Real {
        return Real.of(numberTypeTester.numberToDecimal(this))
    }
}
