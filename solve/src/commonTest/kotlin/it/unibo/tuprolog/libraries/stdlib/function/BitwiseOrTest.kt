package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseOr]
 *
 * @author Enrico
 */
internal class BitwiseOrTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("\\/", 2), BitwiseOr.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
                Integer.of(255),
                BitwiseOr.function(
                        Integer.of(125),
                        Integer.of(255),
                        DummyInstances.executionContext
                )
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseOr)
    }

}
