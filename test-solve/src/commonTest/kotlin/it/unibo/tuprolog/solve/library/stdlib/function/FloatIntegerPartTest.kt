import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.library.stdlib.function.FloatIntegerPart
import it.unibo.tuprolog.solve.library.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.solve.library.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.solve.Signature
import kotlin.test.Test

/**
 * Test class for [FloatIntegerPart]
 *
 * @author Enrico
 */
internal class FloatIntegerPartTest {

    companion object {
        private inline val loggingOn get() = false

        fun <T> assertEquals(expected: T, actual: T) {
            if (loggingOn) println(
                """
                Expecting:
                    $expected
                got
                    $actual
                """.trimIndent()
            )

            kotlin.test.assertEquals(expected, actual)

            if(loggingOn) println("".padEnd(80, '-'))
        }
    }

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("float_integer_part", 1), FloatIntegerPart.signature)
    }

    @Test
    fun computationCorrect() {
        FloatFunctionUtils.numbersToFloatParts.forEach { (input, parts) ->
            val (integerPart, _) = parts
            assertEquals(
                integerPart,
                FloatIntegerPart.computeOf(Numeric.of(input)).`as`<Real>().value.toDouble()
            )
        }
    }

}
