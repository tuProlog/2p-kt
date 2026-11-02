package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [BitwiseLeftShift]
 *
 * @author Enrico
 */
class BitwiseLeftShiftSpec :
    FunSpec({
        test("functor-name-correct") {
            BitwiseLeftShift.signature shouldBe Signature("<<", 2)
        }

        test("computation-correct") {
            BitwiseLeftShift.computeOf(
                Integer.of(16),
                Integer.of(2),
            ) shouldBe Integer.of(64)
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseLeftShift)
        }
    })
