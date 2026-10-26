package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [Exponentiation]
 *
 * @author Enrico
 */
class ExponentiationSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("**", 2), Exponentiation.signature)
        }

        test("computation-correct") {
            assertEquals(
                Numeric.of(-125.0),
                Exponentiation.computeOf(
                    Integer.of(-5),
                    Integer.of(3),
                ),
            )
        }
    })
