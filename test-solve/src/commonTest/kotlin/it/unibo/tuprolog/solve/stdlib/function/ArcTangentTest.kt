package it.unibo.tuprolog.solve.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.stdlib.function.testutils.FunctionUtils.computeOf
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ArcTangent]
 *
 * @author Enrico
 */
internal class ArcTangentTest {
    @Test
    fun functorNameCorrect() {
        assertEquals(Signature("atan", 1), ArcTangent.signature)
    }

    @Test
    fun computationCorrect() {
        assertEquals(
            PI,
            ArcTangent
                .computeOf(Integer.of(1))
                .castToReal()
                .value
                .toDouble() * 4,
        )
    }
}
