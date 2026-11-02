package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.E

/**
 * Test class for [NaturalLogarithm]
 *
 * @author Enrico
 */
class NaturalLogarithmSpec :
    FunSpec({
        test("functor-name-correct") {
            NaturalLogarithm.signature shouldBe Signature("log", 1)
        }

        test("computation-correct") {
            NaturalLogarithm.computeOf(Real.of(E)) shouldBe Numeric.of(1.0)
        }
    })
