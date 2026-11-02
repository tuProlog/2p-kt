package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [BitwiseAnd]
 *
 * @author Enrico
 */
class BitwiseAndSpec :
    FunSpec({
        test("functor-name-correct") {
            BitwiseAnd.signature shouldBe Signature("/\\", 2)
        }

        test("computation-correct") {
            BitwiseAnd.computeOf(
                Integer.of(17 * 256 + 125),
                Integer.of(255),
            ) shouldBe Integer.of(125)
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseAnd)
        }
    })
