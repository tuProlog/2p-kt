package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitiveimpl.testutils.ThrowUtils
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.*

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
            try {
                Throw.primitive(request)
            } catch (e: TuPrologRuntimeException) {
                assertEquals(request.context, e.context)
            }
        }
    }

    @Test
    fun errorCauseChainComputedCorrectly() {
        ThrowUtils.requestSolutionMap.forEach { (request, solutions) ->
            val nextState = StateGoalEvaluation(request, DummyInstances.executionStrategy).behave().toList().single()
            val haltSolution = solutions.single()

            assertEquals(StateEnd.Halt::class, nextState::class)
            assertEquals(haltSolution.exception::class, (nextState as StateEnd.Halt).exception::class)

            var expectedCause = haltSolution.exception.cause
            var actualCause = nextState.exception.cause

            while (expectedCause != null) {
                val expectedCauseStruct = (expectedCause as? PrologError)?.errorStruct
                val actualCauseStruct = (actualCause as? PrologError)?.errorStruct

                assertNotNull(expectedCauseStruct)
                assertNotNull(actualCauseStruct)

                assertTrue("Expected `$expectedCauseStruct` not structurally equals to actual `$actualCauseStruct`") {
                    expectedCauseStruct.structurallyEquals(actualCauseStruct)
                }

                expectedCause = expectedCause.cause
                actualCause = actualCause?.cause
            }
        }
    }
}
