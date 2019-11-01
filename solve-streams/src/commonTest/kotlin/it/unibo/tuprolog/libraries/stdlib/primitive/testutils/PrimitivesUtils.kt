package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateGoalEvaluation
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Utils singleton to help testing Primitive implementations
 *
 * @author Enrico
 */
internal object PrimitivesUtils {

    /**
     * Utility function to test whether the cause of errors generated is correctly filled
     *
     * It passes request to StateGoalEvaluation, then it executes the primitive exercising the error situation;
     * in the end the generated solution's error chain is checked to match with [expectedErrorSolution]'s chain
     */
    internal fun assertErrorCauseChainComputedCorrectly(request: Solve.Request<ExecutionContextImpl>, expectedErrorSolution: Solution.Halt) {
        val nextState = StateGoalEvaluation(request, DummyInstances.executionStrategy).behave().toList().single()

        assertEquals(StateEnd.Halt::class, nextState::class)
        assertEquals(expectedErrorSolution.exception::class, (nextState as StateEnd.Halt).exception::class)

        var expectedCause = expectedErrorSolution.exception.cause
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

    /** Utility method to filter interesting variables returned from a primitive (so without solver filter) */
    internal fun filterInterestingVariables(solution: Solution) = when (solution) {
        is Solution.Yes ->
            // filter substitution
            solution.copy(substitution = solution.substitution.filter { (`var`, _) -> `var` in solution.query.variables })
        else -> solution
    }
}
