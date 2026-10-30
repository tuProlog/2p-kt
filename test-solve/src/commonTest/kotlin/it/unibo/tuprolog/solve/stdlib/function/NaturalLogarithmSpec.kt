package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.E
import kotlin.test.assertEquals

/**
 * Test class for [NaturalLogarithm]
 *
 * @author Enrico
 */
class NaturalLogarithmSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("log", 1), NaturalLogarithm.signature)
        }

        test("computation-correct") {
            assertEquals(
                Numeric.of(1.0),
                NaturalLogarithm.computeOf(Real.of(E)),
            )
        }
    })
