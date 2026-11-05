package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
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
            // cos of the double nearest to pi/2 is 6.12323399573676588613...E-17, correctly rounded to 16 digits
            toBeTested shouldBe (6.123233995736766E-17 plusOrMinus 1E-32)
        }
    })
