package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [BitwiseOr]
 *
 * @author Enrico
 */
class BitwiseOrSpec :
    FunSpec({
        test("functor-name-correct") {
            BitwiseOr.signature shouldBe Signature("\\/", 2)
        }

        test("computation-correct") {
            BitwiseOr.computeOf(
                Integer.of(125),
                Integer.of(255),
            ) shouldBe Integer.of(255)
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseOr)
        }
    })
