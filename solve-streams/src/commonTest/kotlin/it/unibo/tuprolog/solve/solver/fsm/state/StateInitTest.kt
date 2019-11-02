package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.fsm.state.testutils.StateInitUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
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
        assertTrue {
            with(DummyInstances.solveRequest.context) {
                solverStrategies.successCheckStrategy(Signature("true", 0) withArgs emptyList(), this)
            }
        }

        val nextStates = StateInit(StateInitUtils.trueSolveRequest, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.True }
    }

    @Test
    fun ifCurrentGoalIsAVarargPrimitive() {
        val nextStates = StateInit(StateInitUtils.varargPrimitive, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateGoalEvaluation }
    }

    @Test
    fun ifCurrentGoalIsAPrimitiveOrAWellFormedGoal() {
        StateInitUtils.notVarargPrimitiveAndWellFormedGoalRequests
                .filterNot { it == StateInitUtils.preparationNeededGoal }
                .forEach {
                    val nextStates = StateInit(it, DummyInstances.executionStrategy).behave()

                    assertEquals(1, nextStates.count())
                    assertTrue { nextStates.single() is StateGoalEvaluation }

                    assertEquals(it.query, (nextStates.first().solve as Solve.Request<*>).query)
                }
    }

    @Test
    fun ifCurrentGoalNeedPreparationForExecution() {
        val nextStates = StateInit(StateInitUtils.preparationNeededGoal, DummyInstances.executionStrategy).behave()

        assertEquals(
                prepareForExecution(StateInitUtils.preparationNeededGoal.query),
                (nextStates.first().solve as Solve.Request<*>).query
        )
    }

    @Test
    fun ifGoalNotWellFormed() {
        val nextStates = StateInit(StateInitUtils.nonWellFormedGoal, DummyInstances.executionStrategy).behave()

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
