package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseLeftShift]
 *
 * @author Enrico
 */
internal class BitwiseLeftShiftTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("<<", 2), BitwiseLeftShift.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
                Integer.of(64),
                BitwiseLeftShift.function(
                        Integer.of(16),
                        Integer.of(2),
                        DummyInstances.executionContext
                )
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseLeftShift)
    }

}
