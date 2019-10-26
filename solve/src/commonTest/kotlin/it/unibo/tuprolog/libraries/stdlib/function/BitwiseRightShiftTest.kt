package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseRightShift]
 *
 * @author Enrico
 */
internal class BitwiseRightShiftTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature(">>", 2), BitwiseRightShift.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
                Integer.of(4),
                BitwiseRightShift.function(
                        Integer.of(16),
                        Integer.of(2),
                        DummyInstances.executionContext
                )
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseRightShift)
    }

}
