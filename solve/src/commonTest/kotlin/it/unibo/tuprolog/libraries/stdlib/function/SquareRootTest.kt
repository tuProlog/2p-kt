package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
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
                Numeric.of(1.1),
                SquareRoot.function(Real.of(1.21), DummyInstances.executionContext)
        )
    }

}
