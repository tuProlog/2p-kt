package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI
import kotlin.test.assertEquals

/**
 * Test class for [Sine]
 *
 * @author Enrico
 */
class SineSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("sin", 1), Sine.signature)
        }

        test("computation-correct") {
            assertEquals(
                Numeric.of(1.0),
                Sine.computeOf(Numeric.of(PI / 2.0)),
            )
        }
    })
