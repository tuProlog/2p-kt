package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.solve.Signature
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Exponentiation]
 *
 * @author Enrico
 */
internal class ExponentiationTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("**", 2), Exponentiation.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
            Numeric.of(-125.0),
            Exponentiation.computeOf(
                Integer.of(-5),
                Integer.of(3)
            )
        )
    }

}
