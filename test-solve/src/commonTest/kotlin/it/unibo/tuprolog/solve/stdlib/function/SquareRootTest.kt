package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [SquareRoot]
 *
 * @author Enrico
 */
internal class SquareRootTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("sqrt", 1), SquareRoot.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
            Numeric.of("1.1"),
            SquareRoot.computeOf(Real.of("1.21"))
        )
    }
}
