package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.assertEquals

/**
 * Test class for [FloatFractionalPart]
 *
 * @author Enrico
 */
class FloatFractionalPartSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("float_fractional_part", 1), FloatFractionalPart.signature)
        }

        withTests(nameFn = { "computation-correct/${it.id}" }, FloatFunctionUtils.floatPartsCases) { case ->
            assertEquals(case.fractionalPart, FloatFractionalPart.computeOf(Numeric.of(case.input)).castToReal().value)
        }
    })
