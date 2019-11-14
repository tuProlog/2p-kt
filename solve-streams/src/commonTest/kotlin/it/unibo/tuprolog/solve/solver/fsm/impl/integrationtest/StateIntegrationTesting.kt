package it.unibo.tuprolog.solve.solver.fsm.impl.integrationtest

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleWithCutDatabase
import it.unibo.tuprolog.solve.PrologStandardExampleDatabases.prologStandardExampleWithCutDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.customReverseListDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.infiniteComputationDatabaseNotableGoalToSolution
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutAndConjunctionDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleCutDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.impl.StateInit
import it.unibo.tuprolog.solve.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOverFilteredStateInstances
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for testing interactions between states and state machine correct evolution
 *
 * @author Enrico
 */
internal class StateIntegrationTesting {

    /** Shorthand function to execute a solveRequest */
    private fun Solve.Request<ExecutionContextImpl>.executeFSM(): Sequence<State> =
            StateMachineExecutor.execute(StateInit(this))

    /** Utility function to test correct states behaviour inside this class */
    private fun assertSolutionsCorrect(querySolutionsMap: List<Pair<Struct, List<Solution>>>, database: ClauseDatabase) {
        querySolutionsMap.forEach { (goal, solutionList) ->
            val nextStates = createSolveRequest(goal, database, DefaultBuiltins.primitives).executeFSM()

            assertOverFilteredStateInstances<FinalState>(nextStates, { it.solve.solution.query == goal }) { index, finalState ->
                assertSolutionEquals(solutionList[index], finalState.solve.solution)
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
                        StateEnd.False::class
                ),
                nextStates.map { it::class }
        )
    }

    @Test
    fun simpleQueriesWithVariableInstantiationWork() {
        assertSolutionsCorrect(simpleFactDatabaseNotableGoalToSolutions, simpleFactDatabase)
    }

    @Test
    fun simpleQueriesWithDatabaseContainingCutWorksAsExpected() {
        assertSolutionsCorrect(simpleCutDatabaseNotableGoalToSolutions, simpleCutDatabase)
    }

    @Test
    fun simpleQueriesWithCutAndConjunctionDatabase() {
        assertSolutionsCorrect(simpleCutAndConjunctionDatabaseNotableGoalToSolutions, simpleCutAndConjunctionDatabase)
    }

    @Test
    fun queriesWithCutConjunctionAndBacktrackingDatabase() {
        assertSolutionsCorrect(cutConjunctionAndBacktrackingDatabaseNotableGoalToSolutions, cutConjunctionAndBacktrackingDatabase)
    }

    @Test
    fun timeoutExceptionCorrectlyThrown() {
        infiniteComputationDatabaseNotableGoalToSolution.forEach { (goal, solutionList) ->
            val maxDuration = 100L
            val request = Solve.Request(
                    goal.extractSignature(),
                    goal.argsList,
                    ExecutionContextImpl(staticKB = infiniteComputationDatabase),
                    executionMaxDuration = maxDuration
            )
            val nextStates = request.executeFSM()

            assertOverFilteredStateInstances<FinalState>(nextStates, { it.solve.solution.query == goal }) { index, finalState ->
                assertEquals(solutionList[index]::class, finalState.solve.solution::class)
            }
        }
    }

    @Test
    fun prologStandardSearchTreeExample() {
        assertSolutionsCorrect(prologStandardExampleDatabaseNotableGoalToSolution, prologStandardExampleDatabase)
    }

    @Test
    fun prologStandardSearchTreeWithCutExample() {
        assertSolutionsCorrect(prologStandardExampleWithCutDatabaseNotableGoalToSolution, prologStandardExampleWithCutDatabase)
    }

    @Test
    fun testBacktrackingWithCustomReverseListImplementation() {
        assertSolutionsCorrect(customReverseListDatabaseNotableGoalToSolution, customReverseListDatabase)
    }
}
