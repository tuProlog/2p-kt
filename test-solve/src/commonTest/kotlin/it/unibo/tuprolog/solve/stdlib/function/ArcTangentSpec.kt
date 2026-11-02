package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI

/**
 * Test class for [ArcTangent]
 *
 * @author Enrico
 */
class ArcTangentSpec :
    FunSpec({
        test("functor-name-correct") {
            ArcTangent.signature shouldBe Signature("atan", 1)
        }

        test("computation-correct") {
            (
                ArcTangent
                    .computeOf(Integer.of(1))
                    .castToReal()
                    .value
                    .toDouble() * 4
            ) shouldBe PI
        }
    })
