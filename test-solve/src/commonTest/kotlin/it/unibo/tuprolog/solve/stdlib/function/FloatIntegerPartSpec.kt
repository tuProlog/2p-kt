package it.unibo.tuprolog.solve.stdlib.function

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf

/**
 * Test class for [FloatIntegerPart]
 *
 * @author Enrico
 */
class FloatIntegerPartSpec :
    FunSpec({
        test("functor-name-correct") {
            assertEquals(Signature("float_integer_part", 1), FloatIntegerPart.signature)
        }

        withTests(nameFn = { "computation-correct/${it.id}" }, FloatFunctionUtils.floatPartsCases) { case ->
            assertEquals(case.integerPart, FloatIntegerPart.computeOf(Numeric.of(case.input)).castToReal().value)
        }
    })

private inline val loggingOn get() = false

private fun <T> assertEquals(
    expected: T,
    actual: T,
) {
    if (loggingOn) {
        println(
            """
            |Expecting:
            |    $expected
            |got
            |    $actual
            """.trimMargin(),
        )
    }

    kotlin.test.assertEquals(expected, actual)

    if (loggingOn) println("".padEnd(80, '-'))
}
