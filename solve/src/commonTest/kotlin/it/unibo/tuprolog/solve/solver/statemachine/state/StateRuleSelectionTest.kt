package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.solver.SolverUtils
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor.unwrapIfNeeded
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils.multipleMatchesDatabase
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.*

/**
 * Test class for [StateRuleSelection]
 *
 * @author Enrico
 */
internal class StateRuleSelectionTest {

    /** A struct query in the form `f(_)` */
    private val myQueryStruct = Struct.of("f", Var.anonymous())

    @Test
    fun noMatchingRulesFoundGoesIntoFalseState() {
        val nonMatchingRequest = createSolveRequest(myQueryStruct, StateRuleSelectionUtils.singleMatchDatabase)
        val nextStates = StateRuleSelection(nonMatchingRequest, DummyInstances.executionStrategy).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
        assertFalse { nextStates.single().solveRequest.context.isChoicePointChild }
    }

    @Test
    fun makingRuleSelectionBehaveComputesSubStatesOfOneMatchingRuleRequest() {
        val oneMatchRequest = createSolveRequest(Atom.of("b"), StateRuleSelectionUtils.singleMatchDatabase)
        val nextStates = StateRuleSelection(oneMatchRequest, DummyInstances.executionStrategy).behave().toList()

        assertEquals(3, nextStates.count())
        with(nextStates.component1()) {
            assertTrue { this.unwrapIfNeeded() is StateInit }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.first())
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
        with(nextStates.component2()) {
            assertTrue { this.unwrapIfNeeded() is StateEnd.True }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.toList()[1])
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
        with(nextStates.component3()) {
            assertTrue { this is StateEnd.True }
            assertSame(oneMatchRequest.context, this.solveRequest.context.clauseScopedParents.toList()[1])
            assertFalse { this.solveRequest.context.isChoicePointChild }
        }
    }

    @Test
    fun ruleSelectionBehaveWithMultipleChoices() {
        val multipleMatchRequest = createSolveRequest(myQueryStruct, multipleMatchesDatabase)
        val nextStates = StateRuleSelection(multipleMatchRequest, DummyInstances.executionStrategy).behave().toList()
        val databaseClauses = multipleMatchesDatabase.clauses.toList()

        assertEquals(15, nextStates.count())

        val initStates = nextStates.map { it.unwrapIfNeeded() }.filterIsInstance<StateInit>()
        assertEquals(4, initStates.count())
        with(initStates.component1().solveRequest) {
            assertEquals(databaseClauses.component1().body, this.query)
            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.first())
            assertTrue { this.context.isChoicePointChild }
        }
        with(initStates.component2().solveRequest) {
            assertEquals(databaseClauses.component2().body, this.query)
            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.first())
            assertTrue { this.context.isChoicePointChild }
        }
        with(initStates.component3().solveRequest) {
            assertEquals(databaseClauses.component4().body, this.query)
            assertTrue { multipleMatchRequest.context !in this.context.clauseScopedParents }
            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.toList().component2())
            assertFalse { this.context.isChoicePointChild }
        }
        with(initStates.component4().solveRequest) {
            assertEquals(databaseClauses.component3().body, this.query)
            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.first())
            assertTrue { this.context.isChoicePointChild }
        }

        val interestingStates = nextStates
                .filter { it.solveRequest.equalSignatureAndArgs(multipleMatchRequest) }

        assertEquals(1, interestingStates.filterIsInstance<StateEnd.False>().count())
        assertEquals(2, interestingStates.filterIsInstance<StateEnd.True>().count())
    }

    @Test
    fun multipleNestedMatchingRulesTest() {
        val request = createSolveRequest(myQueryStruct, StateRuleSelectionUtils.multipleNestedMatchesDatabase)
        val nextStates = StateRuleSelection(request, DummyInstances.executionStrategy).behave().map { it.unwrapIfNeeded() }.toList()

        val subsequentRuleSelectionStates = nextStates.filterIsInstance<StateRuleSelection>()
        assertEquals(2, subsequentRuleSelectionStates.count())
        nextStates.filterIsInstance<StateInit>().forEach {
            assertEquals(1, it.solveRequest.context.clauseScopedParents.count())
            assertTrue { it.solveRequest.context.isChoicePointChild }
        }

        val interestingEndStates = nextStates.filter { it.solveRequest.equalSignatureAndArgs(request) }.filterIsInstance<StateEnd.True>()
        assertEquals(4, interestingEndStates.count())

        interestingEndStates.forEach {
            assertTrue { request.context in it.solveRequest.context.clauseScopedParents }
        }

        val expectedSubstitutions = listOf("c1", "c2", "d1", "d2").map(Atom.Companion::of)
        val actualSubstitutions = interestingEndStates.map { with(it.solveRequest) { SolverUtils.reduceAndFilterSubstitution(context.substitution, query.variables) }.values.single() }
        expectedSubstitutions.zip(actualSubstitutions).forEach { (expected, actual) ->
            assertEquals(expected, actual)
        }
    }

    // TODO: 31/08/2019 think a test that could fail if matchingRule is not freshCopied
    // TODO: 31/08/2019 think a test that exercises the application of substituion,when passing from rule to another unifying
}
