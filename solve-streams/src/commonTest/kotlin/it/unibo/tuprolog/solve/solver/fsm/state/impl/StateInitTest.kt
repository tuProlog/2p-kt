package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.SolverStrategies
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateInitUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [StateInit]
 *
 * @author Enrico
 */
internal class StateInitTest {

    @Test
    fun ifCurrentGoalMatchesSolverSuccessCheckStrategy() {
        // precondition
        assertTrue { SolverStrategies.prologStandard.successCheckStrategy(Truth.`true`(), DummyInstances.executionContext) }

        val nextStates = StateInit(StateInitUtils.trueSolveRequest).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.True }
    }

    @Test
    fun ifCurrentGoalIsAVarargPrimitive() {
        val nextStates = StateInit(StateInitUtils.varargPrimitive).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateGoalEvaluation }
    }

    @Test
    fun ifCurrentGoalIsAPrimitiveOrAWellFormedGoal() {
        StateInitUtils.notVarargPrimitiveAndWellFormedGoalRequests
                .filterNot { it == StateInitUtils.preparationNeededGoal }
                .forEach {
                    val nextStates = StateInit(it).behave()

                    assertEquals(1, nextStates.count())
                    assertTrue { nextStates.single() is StateGoalEvaluation }

                    assertEquals(it.query, (nextStates.first().solve as Solve.Request<*>).query)
                }
    }

    @Test
    fun ifCurrentGoalNeedPreparationForExecution() {
        val nextStates = StateInit(StateInitUtils.preparationNeededGoal).behave()

        assertEquals(
                prepareForExecution(StateInitUtils.preparationNeededGoal.query),
                (nextStates.first().solve as Solve.Request<*>).query
        )
    }

    @Test
    fun ifGoalNotWellFormed() {
        val nextStates = StateInit(StateInitUtils.nonWellFormedGoal).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
    }

//    @Test
//    fun stateInitializationCreatesNewContextAddingAsParentTheCurrentOne() {
//        val toBeTested = StateInit(DummyInstances.solveRequest, DummyInstances.executionStrategy).behave().single()
//
//        assertSame((toBeTested.solve as Solve.Response).context!!.clauseScopedParents.single(), DummyInstances.executionContext)
//    }
//
//    @Test
//    fun stateInitializationResetsToFalseIsChoicePointChild() {
//        val myState = StateInit(
//                with(DummyInstances.solveRequest) { copy(context = context.copy(isChoicePointChild = true)) },
//                DummyInstances.executionStrategy
//        )
//        val toBeTested = myState.behave().single().solve as Solve.Response
//        assertEquals(false, toBeTested.context!!.isChoicePointChild)
//        assertEquals(true, toBeTested.context!!.clauseScopedParents.single().isChoicePointChild)
//    }
}
