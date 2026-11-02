package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.test.assertAlmostEquals
import kotlin.math.E

/**
 * Test class for [Exponential]
 *
 * @author Enrico
 */
class ExponentialSpec :
    FunSpec({
        test("functor-name-correct") {
            Exponential.signature shouldBe Signature("exp", 1)
        }

        test("computation-correct") {
            assertAlmostEquals(
                Numeric.of(E),
                Exponential.computeOf(Integer.of(1)).castToNumeric(),
            )
        }
    })
