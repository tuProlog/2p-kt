package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
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
                    FloatFractionalPart.function(
                            Numeric.of(input),
                            DummyInstances.executionContext
                    ).`as`<Real>().value.toDouble()
            )
        }
    }

}
