package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [SquareRoot]
 *
 * @author Enrico
 */
class SquareRootSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("sqrt", 1), SquareRoot.signature)
        }

        test("computation-correct") {
            assertEquals(
                Numeric.of("1.1"),
                SquareRoot.computeOf(Real.of("1.21")),
            )
        }
    })
