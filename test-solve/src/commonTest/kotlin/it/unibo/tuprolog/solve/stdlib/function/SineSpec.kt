package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI

/**
 * Test class for [Sine]
 *
 * @author Enrico
 */
class SineSpec :
    FunSpec({
        test("functor-name-correct") {
            Sine.signature shouldBe Signature("sin", 1)
        }

        test("computation-correct") {
            Sine.computeOf(Numeric.of(PI / 2.0)) shouldBe Numeric.of(1.0)
        }
    })
