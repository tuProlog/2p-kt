package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor.unwrapIfNeeded
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalSelectionUtils.composeSignatureAndArgs
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils.createRequestWith
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils.multipleMatchesDatabase
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.*

/**
 * Test class for [StateRuleSelection]
 *
 * @author Enrico
 */
internal class StateRuleSelectionTest {

    private val myQuery = Struct.of("f", Var.anonymous())

    /** `f(_)` request over [StateRuleSelectionUtils.singleMatchDatabase] */
    private val nonMatchingRequest = createRequestWith(myQuery, StateRuleSelectionUtils.singleMatchDatabase)
    /** `b` request over [StateRuleSelectionUtils.singleMatchDatabase] */
    private val oneMatchRequest = createRequestWith(Atom.of("b"), StateRuleSelectionUtils.singleMatchDatabase)
    /** `f(_)` request over [multipleMatchesDatabase] */
    private val multipleMatchRequest = createRequestWith(myQuery, multipleMatchesDatabase)

    @Test
    fun noMatchingRulesFoundGoesIntoFalseState() {
        val nextStates = StateRuleSelection(nonMatchingRequest, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
        assertFalse { nextStates.single().solveRequest.context.isChoicePointChild }
    }

    @Test
    fun makingRuleSelectionBehaveComputesSubStatesOfOneMatchingRuleRequest() {
        val nextStates = StateRuleSelection(oneMatchRequest, DummyInstances.executionStrategy).behave().toList()

        assertEquals(4, nextStates.count())
        with(nextStates.component1()) {
            assertTrue { this.unwrapIfNeeded() is StateInit }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.first())
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
        with(nextStates.component2()) {
            assertTrue { this.unwrapIfNeeded() is StateGoalSelection }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.toList()[1])
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
        with(nextStates.component3()) {
            assertTrue { this.unwrapIfNeeded() is StateEnd.True }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.toList()[1])
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
        with(nextStates.component4()) {
            assertTrue { this is StateEnd.True }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.first())
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
    }

    @Test
    fun ruleSelectionBehaveWithMultipleChoices() {
        val nextStates = StateRuleSelection(multipleMatchRequest, DummyInstances.executionStrategy).behave().toList()
        val databaseClauses = multipleMatchesDatabase.clauses.toList()

        assertEquals(19, nextStates.count())

        val initStates = nextStates.map { it.unwrapIfNeeded() }.filterIsInstance<StateInit>()
        assertEquals(4, initStates.count())
        with(initStates.component1().solveRequest) {
            assertEquals(databaseClauses.component1().body, composeSignatureAndArgs(this))
            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
            assertTrue { this.context.isChoicePointChild }
        }
        with(initStates.component2().solveRequest) {
            assertEquals(databaseClauses.component2().body, composeSignatureAndArgs(this))
            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
            assertTrue { this.context.isChoicePointChild }
        }
        with(initStates.component3().solveRequest) {
            assertEquals(databaseClauses.component4().body, composeSignatureAndArgs(this))
            assertTrue { multipleMatchRequest.context !in this.context.clauseScopedParents }
            assertFalse { this.context.isChoicePointChild }
        }
        with(initStates.component4().solveRequest) {
            assertEquals(databaseClauses.component3().body, composeSignatureAndArgs(this))
            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
            assertTrue { this.context.isChoicePointChild }
        }

        val interestingStates = nextStates
                .filter { it.solveRequest.equalSignatureAndArgs(multipleMatchRequest) }

        assertEquals(1, interestingStates.filterIsInstance<StateEnd.False>().count())
        assertEquals(2, interestingStates.filterIsInstance<StateEnd.True>().count())
    }

    // TODO: 31/08/2019 think a test that could fail if matchingRule is not freshCopied
    // TODO: 31/08/2019 think a test that exercises the application of substituion,when passing from rule to another unifying
}
