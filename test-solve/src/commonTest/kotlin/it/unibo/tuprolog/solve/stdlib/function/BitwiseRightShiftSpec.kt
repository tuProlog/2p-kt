package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [BitwiseRightShift]
 *
 * @author Enrico
 */
class BitwiseRightShiftSpec :
    FunSpec({
        test("functor-name-correct") {
            BitwiseRightShift.signature shouldBe Signature(">>", 2)
        }

        test("computation-correct") {
            BitwiseRightShift.computeOf(
                Integer.of(16),
                Integer.of(2),
            ) shouldBe Integer.of(4)
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseRightShift)
        }
    })
