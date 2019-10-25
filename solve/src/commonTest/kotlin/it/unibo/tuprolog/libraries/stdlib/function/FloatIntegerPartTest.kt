import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.libraries.stdlib.function.FloatIntegerPart
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FloatFunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [FloatIntegerPart]
 *
 * @author Enrico
 */
internal class FloatIntegerPartTest {

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
                    FloatIntegerPart.function(
                            Numeric.of(input),
                            DummyInstances.executionContext
                    ).`as`<Real>().value.toDouble()
            )
        }
    }

}
