package it.unibo.tuprolog.libraries.stdlib.function

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.testutils.DummyInstances
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
                ArcTangent.function(Integer.of(1), DummyInstances.executionContext).`as`<Real>().value.toDouble() * 4
        )
    }

}
