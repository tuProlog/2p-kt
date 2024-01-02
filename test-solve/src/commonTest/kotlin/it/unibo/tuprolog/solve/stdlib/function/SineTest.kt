package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Sine]
 *
 * @author Enrico
 */
internal class SineTest {
    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("sin", 1), Sine.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
            Numeric.of(1.0),
            Sine.computeOf(Numeric.of(PI / 2.0)),
        )
    }
}
