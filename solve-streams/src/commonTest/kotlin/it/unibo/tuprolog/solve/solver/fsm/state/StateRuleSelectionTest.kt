package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.solver.fsm.StateMachineExecutor.unwrapIfNeeded
import it.unibo.tuprolog.solve.solver.fsm.state.testutils.StateRuleSelectionUtils
import it.unibo.tuprolog.solve.solver.fsm.state.testutils.StateRuleSelectionUtils.multipleMatchesDatabase
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val nextStates = StateRuleSelection(nonMatchingRequest).behave()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.single() is StateEnd.False }
//        assertFalse { (nextStates.single().solve as Solve.Response).context!!.isChoicePointChild }
    }

    @Test
    fun makingRuleSelectionBehaveComputesSubStatesOfOneMatchingRuleRequest() {
        val oneMatchRequest = createSolveRequest(Atom.of("b"), StateRuleSelectionUtils.singleMatchDatabase)
        val nextStates = StateRuleSelection(oneMatchRequest).behave().toList()

        assertEquals(3, nextStates.count())
        with(nextStates.component1()) {
            assertTrue { this.unwrapIfNeeded() is StateInit }
//            assertSame(oneMatchRequest.context, (this.solve as Solve.Request<ExecutionContextImpl>).context.clauseScopedParents.first())
//            assertFalse { (this.solve as Solve.Request<ExecutionContextImpl>).context.isChoicePointChild }
        }
        with(nextStates.component2()) {
            assertTrue { this.unwrapIfNeeded() is StateEnd.True }
//            assertSame(oneMatchRequest.context, (this.solve as Solve.Response).context!!.clauseScopedParents.toList()[1])
//            assertFalse { (this.solve as Solve.Response).context!!.isChoicePointChild }
        }
        with(nextStates.component3()) {
            assertTrue { this is StateEnd.True }
//            assertSame(oneMatchRequest.context, (this.solve as Solve.Response).context!!.clauseScopedParents.toList()[1])
//            assertFalse { (this.solve as Solve.Response).context!!.isChoicePointChild }
        }
    }

    @Test
    fun ruleSelectionBehaveWithMultipleChoices() {
        val multipleMatchRequest = createSolveRequest(myQueryStruct, multipleMatchesDatabase)
        val nextStates = StateRuleSelection(multipleMatchRequest).behave().toList()
        val databaseClauses = multipleMatchesDatabase.clauses.toList()

        assertEquals(14, nextStates.count())

        val initStates = nextStates.map { it.unwrapIfNeeded() }.filterIsInstance<StateInit>()
        assertEquals(4, initStates.count())
        with(initStates.component1().solve) {
            assertEquals(databaseClauses.component1().body, this.query)
//            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
//            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.first())
//            assertTrue { this.context.isChoicePointChild }
        }
        with(initStates.component2().solve) {
            assertEquals(databaseClauses.component2().body, this.query)
//            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
//            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.first())
//            assertTrue { this.context.isChoicePointChild }
        }
        with(initStates.component3().solve) {
            assertEquals(databaseClauses.component4().body, this.query)
//            assertTrue { multipleMatchRequest.context !in this.context.clauseScopedParents }
//            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.toList().component2())
//            assertFalse { this.context.isChoicePointChild }
        }
        with(initStates.component4().solve) {
            assertEquals(databaseClauses.component3().body, this.query)
//            assertEquals(multipleMatchRequest.context, this.context.clauseScopedParents.first())
//            assertEquals(multipleMatchRequest, this.context.logicalParentRequests.first())
//            assertTrue { this.context.isChoicePointChild }
        }

        val interestingStates = nextStates
                .filter { it is FinalState && it.solve.solution.query == multipleMatchRequest.query }

        assertEquals(2, interestingStates.filterIsInstance<StateEnd.True>().count())
    }

    @Test
    fun multipleNestedMatchingRulesTest() {
        val request = createSolveRequest(myQueryStruct, StateRuleSelectionUtils.multipleNestedMatchesDatabase)
        val nextStates = StateRuleSelection(request).behave().map { it.unwrapIfNeeded() }.toList()

        val subsequentRuleSelectionStates = nextStates.filterIsInstance<StateRuleSelection>()
        assertEquals(2, subsequentRuleSelectionStates.count())
//        nextStates.filterIsInstance<StateInit>().forEach {
//            assertEquals(1, it.solve.context.clauseScopedParents.count())
//            assertTrue { it.solve.context.isChoicePointChild }
//        }

        val interestingEndStates = nextStates.filter { it is StateEnd.True && it.solve.solution.query == request.query }
        assertEquals(4, interestingEndStates.count())

//        interestingEndStates.forEach {
//            assertTrue { request.context in (it as FinalState).solve.context!!.clauseScopedParents }
//        }

        val expectedSubstitutions = listOf("c1", "c2", "d1", "d2").map(Atom.Companion::of)
        val actualSubstitutions = interestingEndStates.map { with((it as FinalState).solve) { solution.substitution.filter { (`var`, _) -> `var` in solution.query.variables } }.values.single() }
        expectedSubstitutions.zip(actualSubstitutions).forEach { (expected, actual) ->
            assertEquals(expected, actual)
        }
    }

    // TODO: 31/08/2019 think a test that could fail if matchingRule is not freshCopied
    // TODO: 31/08/2019 think a test that exercises the application of substituion,when passing from rule to another unifying
}
