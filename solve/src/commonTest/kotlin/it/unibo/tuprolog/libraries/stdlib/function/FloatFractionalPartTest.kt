package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.primitive.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [FloatFractionalPart]
 *
 * @author Enrico
 */
internal class FloatFractionalPartTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("float_fractional_part", 1), FloatFractionalPart.signature)
    }

    @Test
    fun computationCorrect() {
        FloatFunctionUtils.numbersToFloatParts.forEach { (input, parts) ->
            val (_, fractionalPart) = parts
            assertEquals(
                fractionalPart,
                FloatFractionalPart.computeOf(Numeric.of(input)).`as`<Real>().value.toDouble()
            )
        }
    }

}
