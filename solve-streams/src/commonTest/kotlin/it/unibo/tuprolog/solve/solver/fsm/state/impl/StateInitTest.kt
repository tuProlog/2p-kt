package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.SolverStrategies
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils.allRequests
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils.allWellFormedGoalRequests
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils.nonWellFormedGoalRequest
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils.trueRequest
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils.wellFormedGoalRequestsNeedingPreparationForExecution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils.wellFormedGoalRequestsNotNeedingPreparationForExecution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateUtils.getSideEffectsManager
import it.unibo.tuprolog.solve.solver.prepareForExecutionAsGoal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [StateInit]
 *
 * @author Enrico
 */
internal class StateInitTest {

    @Test
    fun trueGoalGoesIntoTrueEndState() {
        // precondition
        assertTrue { SolverStrategies.prologStandard.successCheckStrategy(Truth.`true`(), DummyInstances.executionContext) }

        val nextStates = StateInit(trueRequest).behave()

        assertOnlyOneNextState<StateEnd.True>(nextStates)
    }

    @Test
    fun wellFormedGoalsGoIntoStateGoalEvaluation() {
        allWellFormedGoalRequests.forEach {
            val nextStates = StateInit(it).behave()

            assertOnlyOneNextState<StateGoalEvaluation>(nextStates)
        }
    }

    @Test
    fun wellFormedGoalsNotNeedingPreparationForExecutionAreTheSameInNextStateRequest() {
        wellFormedGoalRequestsNotNeedingPreparationForExecution
                .forEach {
                    val nextStates = StateInit(it).behave()

                    assertEquals(it.query, (nextStates.single().solve as Solve.Request<*>).query)
                }
    }

    @Test
    fun wellFormedGoalsNeedingPreparationForExecutionArePreparedInNextStateRequest() {
        wellFormedGoalRequestsNeedingPreparationForExecution
                .forEach {
                    val nextStates = StateInit(it).behave()

                    assertEquals(
                            it.query.prepareForExecutionAsGoal(),
                            (nextStates.single().solve as Solve.Request<*>).query
                    )
                }
    }

    @Test
    fun notWellFormedGoalGoesInStateEndFalse() {
        val nextStates = StateInit(nonWellFormedGoalRequest).behave()

        assertOnlyOneNextState<StateEnd.False>(nextStates)
    }

    @Test
    fun nextStateSideEffectManagerHasBeenInitializedHenceIsNotEquals() {
        allRequests.forEach {
            val beforeSideEffectManager = it.context.sideEffectManager

            val nextState = StateInit(it).behave().single()

            assertNotEquals(beforeSideEffectManager, nextState.solve.getSideEffectsManager())
        }
    }
}
