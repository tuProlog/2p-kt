package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.ExecutionContext
import org.gciatto.kt.math.BigDecimal
import kotlin.math.pow


/**
 * Implementation of `'**'/2` arithmetic functor
 *
 * @author Enrico
 */
object Exponentiation : BinaryMathFunction("**") {

    // TODO: 24/10/2019 missing "float_overflow", "underflow" and "undefined" error checks (see the standard)

    override fun mathFunction(integer1: Integer, integer2: Integer, context: ExecutionContext): Numeric =
        returnOneOfBothZero(integer1, integer2)
            ?: Real.of(integer1.decimalValue.pow(integer2.value.toInt()))

    override fun mathFunction(real: Real, integer: Integer, context: ExecutionContext): Numeric =
        returnOneOfBothZero(real, integer)
            ?: Real.of(real.value.pow(integer.value.toInt()))

    override fun mathFunction(integer: Integer, real: Real, context: ExecutionContext): Numeric =
        returnOneOfBothZero(integer, real)
            ?: Real.of(integer.value.toDouble().pow(real.value.toDouble()))

    override fun mathFunction(real1: Real, real2: Real, context: ExecutionContext): Numeric =
        returnOneOfBothZero(real1, real2)
            ?: Real.of(real1.value.toDouble().pow(real2.value.toDouble()))

    /** Implements the check for both zeros */
    private fun returnOneOfBothZero(numeric1: Numeric, numeric2: Numeric) = when {
        numeric1.decimalValue == BigDecimal.ZERO && numeric2.decimalValue == BigDecimal.ZERO -> Numeric.of(1.0)
        else -> null
    }
}
