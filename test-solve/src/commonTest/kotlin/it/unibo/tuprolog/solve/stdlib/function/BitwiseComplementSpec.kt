package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [BitwiseComplement]
 *
 * @author Enrico
 */
class BitwiseComplementSpec :
    FunSpec({
        test("functor-name-correct") {
            BitwiseComplement.signature shouldBe Signature("\\", 1)
        }

        test("computation-correct") {
            BitwiseComplement.computeOf(BitwiseComplement.computeOf(Integer.of(10))) shouldBe Integer.of(10)
        }

        test("rejected-inputs") {
            FunctionUtils.assertRejectsNonIntegerParameters(BitwiseComplement)
        }
    })
