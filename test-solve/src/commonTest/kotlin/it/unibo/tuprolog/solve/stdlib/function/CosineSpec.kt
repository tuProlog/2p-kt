package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI

/**
 * Test class for [Cosine]
 *
 * @author Enrico
 */
class CosineSpec :
    FunSpec({
        test("functor-name-correct") {
            Cosine.signature shouldBe Signature("cos", 1)
        }

        test("computation-correct") {
            val toBeTested =
                Cosine
                    .computeOf(Real.of(PI / 2.0))
                    .castToNumeric()
                    .decimalValue
                    .toDouble()
            withClue("Cosine of \"PI/2\" should be closer to Zero") {
                (
                    toBeTested < 6.123233995736767E-17 &&
                        toBeTested > 6.123233995736765E-17
                ).shouldBeTrue()
            }
        }
    })
