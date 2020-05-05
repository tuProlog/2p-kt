package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.assertPrologError
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ThrowUtils
import kotlin.test.Test

/**
 * Test class for [Throw]
 *
 * @author Enrico
 */
internal class ThrowTest {

    @Test
    fun throwPrimitiveThrowCorrectErrors() {
        ThrowUtils.errorThrowingBehaviourRequest.forEach { (request, errorType) ->
            assertPrologError(errorType, Throw.wrappedImplementation(request).single().solution)
        }
    }
}
