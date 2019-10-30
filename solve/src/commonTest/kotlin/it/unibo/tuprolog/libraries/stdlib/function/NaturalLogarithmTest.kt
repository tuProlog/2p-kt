package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.primitive.Signature
import kotlin.math.E
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [NaturalLogarithm]
 *
 * @author Enrico
 */
internal class NaturalLogarithmTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("log", 1), NaturalLogarithm.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
                Numeric.of(1.0),
                NaturalLogarithm.computeOf(Real.of(E))
        )
    }

}
