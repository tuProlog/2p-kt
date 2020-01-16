package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.PrimitiveUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.ThrowUtils
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Throw]
 *
 * @author Enrico
 */
internal class ThrowTest {

    @Test
    fun throwPrimitiveThrowCorrectErrors() {
        ThrowUtils.exposedErrorThrowingBehaviourRequest.forEach { (request, errorType) ->
            assertFailsWith(errorType) { Throw.wrappedImplementation(request) }
        }
    }

    @Test
    fun throwPrimitiveErrorContainsCorrectContext() {
        ThrowUtils.exposedErrorThrowingBehaviourRequest.forEach { (request, _) ->
            assertOverFailure<TuPrologRuntimeException>({ Throw.wrappedImplementation(request) }) {
                assertEquals(request.context, it.context)
            }
        }
    }
}
