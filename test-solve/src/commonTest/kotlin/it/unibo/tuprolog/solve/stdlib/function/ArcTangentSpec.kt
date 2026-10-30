package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI
import kotlin.test.assertEquals

/**
 * Test class for [ArcTangent]
 *
 * @author Enrico
 */
class ArcTangentSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("atan", 1), ArcTangent.signature)
        }

        test("computation-correct") {
            assertEquals(
                PI,
                ArcTangent
                    .computeOf(Integer.of(1))
                    .castToReal()
                    .value
                    .toDouble() * 4,
            )
        }
    })
