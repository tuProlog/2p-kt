package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseAnd]
 *
 * @author Enrico
 */
class BitwiseAndSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("/\\", 2), BitwiseAnd.signature)
        }

        test("computation-correct") {
            assertEquals(
                Integer.of(125),
                BitwiseAnd.computeOf(
                    Integer.of(17 * 256 + 125),
                    Integer.of(255),
                ),
            )
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseAnd)
        }
    })
