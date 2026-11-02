package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [SquareRoot]
 *
 * @author Enrico
 */
class SquareRootSpec :
    FunSpec({
        test("functor-name-correct") {
            SquareRoot.signature shouldBe Signature("sqrt", 1)
        }

        test("computation-correct") {
            SquareRoot.computeOf(Real.of("1.21")) shouldBe Numeric.of("1.1")
        }
    })
