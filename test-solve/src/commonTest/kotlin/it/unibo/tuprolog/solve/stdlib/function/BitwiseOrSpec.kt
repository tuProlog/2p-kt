package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseOr]
 *
 * @author Enrico
 */
class BitwiseOrSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("\\/", 2), BitwiseOr.signature)
        }

        test("computation-correct") {
            assertEquals(
                Integer.of(255),
                BitwiseOr.computeOf(
                    Integer.of(125),
                    Integer.of(255),
                ),
            )
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseOr)
        }
    })
