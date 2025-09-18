package it.unibo.tuprolog.solve.streams.primitive

import it.unibo.tuprolog.solve.streams.primitive.testutils.PrimitiveUtils.assertPrologError
import it.unibo.tuprolog.solve.streams.primitive.testutils.ThrowUtils
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw
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
            assertPrologError(
                errorType,
                Throw.implementation
                    .solve(request)
                    .single()
                    .solution,
            )
        }
    }
}
