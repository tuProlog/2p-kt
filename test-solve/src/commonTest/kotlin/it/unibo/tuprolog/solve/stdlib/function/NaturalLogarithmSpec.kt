package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import org.gciatto.kt.math.MathContext
import org.gciatto.kt.math.big.e
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
            // kotlin.math.E is slightly below e: its logarithm, correctly rounded, is 0.9999999999999999
            NaturalLogarithm.computeOf(Real.of(E)) shouldBe Real.of("0.9999999999999999")
            NaturalLogarithm.computeOf(Real.of(e(MathContext(40)))) shouldBe Numeric.of(1.0)
        }

        test("computation-correct/near-one") {
            // log(1 + 1E-10) is 9.9999999995000000000333...E-11: cancellation must not cost digits
            NaturalLogarithm.computeOf(Real.of("1.0000000001")) shouldBe Real.of("9.999999999500000E-11")
        }
    })
