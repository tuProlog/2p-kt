package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.test.assertAlmostEquals
import kotlin.math.E
import kotlin.test.assertEquals

/**
 * Test class for [Exponential]
 *
 * @author Enrico
 */
class ExponentialSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("exp", 1), Exponential.signature)
        }

        test("computation-correct") {
            assertAlmostEquals(
                Numeric.of(E),
                Exponential.computeOf(Integer.of(1)).castToNumeric(),
            )
        }
    })
