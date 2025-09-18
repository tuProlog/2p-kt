package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Cosine]
 *
 * @author Enrico
 */
internal class CosineTest {
    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("cos", 1), Cosine.signature)
    }

    @Test
    fun computationCorrect() {
        val toBeTested =
            Cosine
                .computeOf(Real.of(PI / 2.0))
                .castToNumeric()
                .decimalValue
                .toDouble()
        assertTrue("Cosine of \"PI/2\" should be closer to Zero") {
            toBeTested < 6.123233995736767E-17 && toBeTested > 6.123233995736765E-17
        }
    }
}
