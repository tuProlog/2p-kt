package it.unibo.tuprolog.solve.library.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.library.stdlib.function.testutils.FunctionUtils
import it.unibo.tuprolog.solve.library.stdlib.function.testutils.FunctionUtils.computeOf
import it.unibo.tuprolog.solve.primitive.Signature
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
            BitwiseLeftShift.computeOf(
                Integer.of(16),
                Integer.of(2)
            )
        )
    }

    @Test
    fun rejectedInputs() {
        FunctionUtils.assertRejectsNonIntegerParameters(BitwiseLeftShift)
    }

}
