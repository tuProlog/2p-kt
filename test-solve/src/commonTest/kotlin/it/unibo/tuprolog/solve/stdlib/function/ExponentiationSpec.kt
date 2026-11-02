package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [Exponentiation]
 *
 * @author Enrico
 */
class ExponentiationSpec :
    FunSpec({
        test("functor-name-correct") {
            Exponentiation.signature shouldBe Signature("**", 2)
        }

        test("computation-correct") {
            Exponentiation.computeOf(
                Integer.of(-5),
                Integer.of(3),
            ) shouldBe Numeric.of(-125.0)
        }
    })
