package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseAnd]
 *
 * @author Enrico
 */
internal class BitwiseAndTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("/\\", 2), BitwiseAnd.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
                Integer.of(125),
                BitwiseAnd.function(
                        Integer.of(17 * 256 + 125),
                        Integer.of(255),
                        DummyInstances.executionContext
                )
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseAnd)
    }

}
