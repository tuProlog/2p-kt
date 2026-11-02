package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [FloatFractionalPart]
 *
 * @author Enrico
 */
class FloatFractionalPartSpec :
    FunSpec({
        test("functor-name-correct") {
            FloatFractionalPart.signature shouldBe Signature("float_fractional_part", 1)
        }

        withTests(nameFn = { "computation-correct/${it.id}" }, FloatFunctionUtils.floatPartsCases) { case ->
            FloatFractionalPart.computeOf(Numeric.of(case.input)).castToReal().value shouldBe case.fractionalPart
        }
    })
