package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.solve.Signature
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
            BitwiseComplement.computeOf(BitwiseComplement.computeOf(Integer.of(10)))
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseComplement)
    }

}
