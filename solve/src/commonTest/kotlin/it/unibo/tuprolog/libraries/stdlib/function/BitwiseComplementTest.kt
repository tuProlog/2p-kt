package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.libraries.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [BitwiseComplement]
 *
 * @author Enrico
 */
internal class BitwiseComplementTest {

    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("\\", 1), BitwiseComplement.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
                Integer.of(10),
                BitwiseComplement.function(
                        BitwiseComplement.function(
                                Integer.of(10), DummyInstances.executionContext), DummyInstances.executionContext)
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseComplement)
    }

}
