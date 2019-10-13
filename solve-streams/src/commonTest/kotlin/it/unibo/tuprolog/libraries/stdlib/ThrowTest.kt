package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.stdlib.testutils.PrimitivesUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.libraries.stdlib.testutils.PrimitivesUtils.assertRequestContextEqualToThrownErrorOne
import it.unibo.tuprolog.libraries.stdlib.testutils.ThrowUtils
import kotlin.test.Test
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
            assertFailsWith(errorType) { Throw.primitive(request) }
        }
    }

    @Test
    fun throwPrimitiveErrorContainsCorrectContext() {
        ThrowUtils.exposedErrorThrowingBehaviourRequest.forEach { (request, _) ->
            assertRequestContextEqualToThrownErrorOne(request, Throw)
        }
    }

    @Test
    fun errorCauseChainComputedCorrectly() {
        ThrowUtils.requestSolutionMap.forEach { (request, solutions) ->
            assertErrorCauseChainComputedCorrectly(request, solutions.single())
        }
    }
}
