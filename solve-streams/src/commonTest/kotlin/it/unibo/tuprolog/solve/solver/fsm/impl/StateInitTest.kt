package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.SolverStrategies
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateInitUtils.allRequests
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateInitUtils.allWellFormedGoalRequests
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateInitUtils.nonWellFormedGoalRequest
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateInitUtils.trueRequest
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateInitUtils.wellFormedGoalRequestsNeedingPreparationForExecution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateInitUtils.wellFormedGoalRequestsNotNeedingPreparationForExecution
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.getSideEffectsManager
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
        assertTrue {
            SolverStrategies.prologStandard.successCheckStrategy(
                Truth.TRUE,
                DummyInstances.executionContext
            )
        }

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
        fun prepareGoalForExecution(goal: Term) = Directive.of(goal).prepareForExecution().body

        wellFormedGoalRequestsNeedingPreparationForExecution
            .forEach {
                val nextStates = StateInit(it).behave()

                assertEquals(
                    prepareGoalForExecution(it.query),
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
