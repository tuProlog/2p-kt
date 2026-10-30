package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseComplement]
 *
 * @author Enrico
 */
class BitwiseComplementSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("\\", 1), BitwiseComplement.signature)
        }

        test("computation-correct") {
            assertEquals(
                Integer.of(10),
                BitwiseComplement.computeOf(BitwiseComplement.computeOf(Integer.of(10))),
            )
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseComplement)
        }
    })
