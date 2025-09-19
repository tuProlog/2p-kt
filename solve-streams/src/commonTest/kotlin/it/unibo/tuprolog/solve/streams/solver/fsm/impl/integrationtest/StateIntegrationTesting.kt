package it.unibo.tuprolog.solve.streams.solver.fsm.impl.integrationtest

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleWithCutTheory
import it.unibo.tuprolog.solve.PrologStandardExampleTheories.prologStandardExampleWithCutTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.TestingClauseTheories.customReverseListTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.customReverseListTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.cutConjunctionAndBacktrackingTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.infiniteComputationTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.infiniteComputationTheoryNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutAndConjunctionTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleCutTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.FinalState
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateInit
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateUtils.assertOverFilteredStateInstances
import it.unibo.tuprolog.solve.streams.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for testing interactions between states and state machine correct evolution
 *
 * @author Enrico
 */
internal class StateIntegrationTesting {
    /** Shorthand function to execute a solveRequest */
    private fun Solve.Request<StreamsExecutionContext>.executeFSM(): Sequence<State> =
        StateMachineExecutor.execute(
            StateInit(this),
        )

    /** Utility function to test correct states behaviour inside this class */
    private fun assertSolutionsCorrect(
        querySolutionsMap: List<Pair<Struct, List<Solution>>>,
        database: Theory,
    ) {
        querySolutionsMap.forEach { (goal, solutionList) ->
            val nextStates = createSolveRequest(goal, database, DefaultBuiltins.primitives).executeFSM()

            assertOverFilteredStateInstances<FinalState>(
                nextStates,
                { it.solve.solution.query == goal },
            ) { index, finalState ->
                assertSolutionEquals(
                    solutionList[index],
                    finalState.solve.solution.let {
                        // cleanUp as in StreamsSolver
                        (it as? Solution.Yes)?.copy(substitution = it.substitution.filter { _, t -> t !is Var }) ?: it
                    },
                )
            }
        }
    }

    @Test
    fun trueSolveRequestWorks() {
        val nextStates = createSolveRequest(Atom.of("true")).executeFSM()

        assertOnlyOneNextState<StateEnd.True>(nextStates)
    }

    @Test
    fun nonPresentClause() {
        val nextStates = createSolveRequest(Atom.of("ciao")).executeFSM().toList()

        assertEquals(
            listOf(
                StateGoalEvaluation::class,
                StateRuleSelection::class,
                StateEnd.False::class,
            ),
            nextStates.map { it::class },
        )
    }

    @Test
    fun simpleQueriesWithVariableInstantiationWork() {
        assertSolutionsCorrect(simpleFactTheoryNotableGoalToSolutions, simpleFactTheory)
    }

    @Test
    fun simpleQueriesWithTheoryContainingCutWorksAsExpected() {
        assertSolutionsCorrect(simpleCutTheoryNotableGoalToSolutions, simpleCutTheory)
    }

    @Test
    fun simpleQueriesWithCutAndConjunctionTheory() {
        assertSolutionsCorrect(simpleCutAndConjunctionTheoryNotableGoalToSolutions, simpleCutAndConjunctionTheory)
    }

    @Test
    fun queriesWithCutConjunctionAndBacktrackingTheory() {
        assertSolutionsCorrect(
            cutConjunctionAndBacktrackingTheoryNotableGoalToSolutions,
            cutConjunctionAndBacktrackingTheory,
        )
    }

    @Test
    fun timeoutExceptionCorrectlyThrown() {
        infiniteComputationTheoryNotableGoalToSolution.forEach { (goal, solutionList) ->
            val maxDuration = 100L
            val request =
                Solve.Request(
                    goal.extractSignature(),
                    goal.args,
                    StreamsExecutionContext(staticKb = infiniteComputationTheory),
                    maxDuration = maxDuration,
                )
            val nextStates = request.executeFSM()

            assertOverFilteredStateInstances<FinalState>(
                nextStates,
                { it.solve.solution.query == goal },
            ) { index, finalState ->
                assertEquals(solutionList[index]::class, finalState.solve.solution::class)
            }
        }
    }

    @Test
    @Ignore
    fun prologStandardSearchTreeExample() {
        assertSolutionsCorrect(prologStandardExampleTheoryNotableGoalToSolution, prologStandardExampleTheory)
    }

    @Test
    @Ignore
    fun prologStandardSearchTreeWithCutExample() {
        assertSolutionsCorrect(
            prologStandardExampleWithCutTheoryNotableGoalToSolution,
            prologStandardExampleWithCutTheory,
        )
    }

    @Test
    fun testBacktrackingWithCustomReverseListImplementation() {
        assertSolutionsCorrect(customReverseListTheoryNotableGoalToSolution, customReverseListTheory)
    }
}
