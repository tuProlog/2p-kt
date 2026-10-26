package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseRightShift]
 *
 * @author Enrico
 */
class BitwiseRightShiftSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature(">>", 2), BitwiseRightShift.signature)
        }

        test("computation-correct") {
            assertEquals(
                Integer.of(4),
                BitwiseRightShift.computeOf(
                    Integer.of(16),
                    Integer.of(2),
                ),
            )
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseRightShift)
        }
    })
