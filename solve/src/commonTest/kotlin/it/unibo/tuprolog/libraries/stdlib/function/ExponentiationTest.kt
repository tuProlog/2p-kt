package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
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
                Exponentiation.function(
                        Integer.of(-5),
                        Integer.of(3),
                        DummyInstances.executionContext
                )
        )
    }

}
