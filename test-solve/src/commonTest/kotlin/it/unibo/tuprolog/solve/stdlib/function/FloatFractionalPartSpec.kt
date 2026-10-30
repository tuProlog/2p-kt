package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [FloatFractionalPart]
 *
 * @author Enrico
 */
class FloatFractionalPartSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("float_fractional_part", 1), FloatFractionalPart.signature)
        }

        test("computation-correct") {
            FloatFunctionUtils.numbersToFloatParts.forEach { (input, parts) ->
                val (_, fractionalPart) = parts
                assertEquals(
                    fractionalPart,
                    FloatFractionalPart.computeOf(Numeric.of(input)).castToReal().value,
                )
            }
        }
    })
