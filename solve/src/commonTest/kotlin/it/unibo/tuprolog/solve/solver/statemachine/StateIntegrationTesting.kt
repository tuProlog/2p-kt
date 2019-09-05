package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.*
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateGoalSelectionUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for testing interactions between states and state machine correct evolution
 *
 * @author Enrico
 */
internal class StateIntegrationTesting {

    /** Shorthand function to execute a solveRequest */
    private fun execute(solveRequest: Solve.Request): Sequence<State> =
            StateMachineExecutor.execute(StateInit(solveRequest, DummyInstances.executionStrategy))

    @Test
    fun trueSolveRequestWorks() {
        val nextStates = execute(StateGoalSelectionUtils.trueSolveRequest).toList()

        assertEquals(2, nextStates.count())
        assertTrue { nextStates.component1() is StateGoalSelection }
        assertTrue { nextStates.component2() is StateEnd.True }
    }

    @Test
    fun nonPresentClause() {
        val nextStates = execute(StateGoalSelectionUtils.failSolveRequest).toList()

        assertEquals(4, nextStates.count())
        assertTrue { nextStates.component1() is StateGoalSelection }
        assertTrue { nextStates.component2() is StateGoalEvaluation }
        assertTrue { nextStates.component3() is StateRuleSelection }
        assertTrue { nextStates.component4() is StateEnd.False }
    }

    @Test
    fun requiringSimpleQueryWithVariableInstantiationWorks() {
        val nextStates = execute(SolverTestUtils.oneResponseRequest).toList()

        assertEquals(7, nextStates.count())
        assertTrue { nextStates[0] is StateGoalSelection }
        assertTrue { nextStates[1] is StateGoalEvaluation }
        assertTrue { nextStates[2] is StateRuleSelection }
        assertTrue { nextStates[3] is StateInit }
        assertTrue { nextStates[4] is StateGoalSelection }
        assertTrue { nextStates[5] is StateEnd.True }
        assertTrue { nextStates[6] is StateEnd.True }

        val answerSubstitution = (nextStates[6] as StateEnd.True).answerSubstitution
        val scope = Scope.of(*answerSubstitution.keys.toTypedArray())

        assertEquals(Atom.of("a"), answerSubstitution[scope.varOf("A")])
        assertEquals(1, answerSubstitution.count())
    }

    @Test
    fun requiringQueryWithMultipleInstantiationsWorks() {
        val nextStates = execute(SolverTestUtils.threeResponseRequest).toList()

        assertEquals(15, nextStates.count())

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(6, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solveRequest.equalSignatureAndArgs(SolverTestUtils.threeResponseRequest) }
        assertEquals(3, interestingStates.count())

        val answerSubstitution = interestingStates.map { it.answerSubstitution }

        val correctSubstitution = listOf("a", "b", "c").map(Atom.Companion::of)
        correctSubstitution.zip(answerSubstitution).forEach { (expectedSubstituent, currentSubstitution) ->
            val currentScope = Scope.of(*currentSubstitution.keys.toTypedArray())

            assertEquals(expectedSubstituent, currentSubstitution[currentScope.varOf("A")])
            assertEquals(1, currentSubstitution.count())
        }
    }

    @Test
    fun requiringQueryWithMultipleInstantiationsHasStateInternalsCorrect() {
        val nextStates = execute(SolverTestUtils.threeResponseRequest).toList()
        assertEquals(15, nextStates.count())

        val subInitStates = nextStates.filterIsInstance<StateInit>()
        assertEquals(3, subInitStates.count())
        assertTrue { subInitStates.all { it.solveRequest.context.isChoicePointChild } }
        assertTrue { (nextStates - subInitStates).none { it.solveRequest.context.isChoicePointChild } }

        val subRuleSelectionStateContexts = nextStates.filterIsInstance<StateRuleSelection>().map { it.solveRequest.context }
        subRuleSelectionStateContexts.zip(subInitStates.map { it.solveRequest.context }).forEach { (expected, actual) ->
            assertSame(expected, actual.clauseScopedParents.first())
        }

        subRuleSelectionStateContexts.zip(subInitStates.map { it.solveRequest.context }).forEach { (ruleSelectionContext, initContext) ->
            assertSame(ruleSelectionContext, initContext.clauseScopedParents.first())
        }
    }

    @Test
    fun requiringQueryWithCutDoesntReturnOtherAlternatives() {
        val nextStates = execute(SolverTestUtils.oneResponseBecauseOfCut).toList()

        assertEquals(8, nextStates.count())

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(2, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solveRequest.equalSignatureAndArgs(SolverTestUtils.oneResponseBecauseOfCut) }
        assertEquals(1, interestingStates.count())
        assertEquals(Atom.of("only"), interestingStates.single().answerSubstitution.values.single())
    }

    @Test
    fun twoAlternativesBeforeCutExecutionShouldBeReturned() {
        val nextStates = execute(SolverTestUtils.twoResponseBecauseOfCut).toList()

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(4, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solveRequest.equalSignatureAndArgs(SolverTestUtils.twoResponseBecauseOfCut) }
        assertEquals(2, interestingStates.count())
        assertEquals(
                listOf(Atom.of("a"), Atom.of("only")),
                interestingStates.map { it.answerSubstitution.values.single() }
        )
    }

    @Test
    fun threeAlternativesBecauseOfInternalCutExecution() {
        val nextStates = execute(SolverTestUtils.threeResponseBecauseOfCut).toList()

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(9, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solveRequest.equalSignatureAndArgs(SolverTestUtils.threeResponseBecauseOfCut) }
        assertEquals(3, interestingStates.count())
        assertEquals(
                listOf(Atom.of("a"), Atom.of("c"), Atom.of("d")),
                interestingStates.map { it.answerSubstitution.values.single() }
        )
    }

    @Test
    fun twoResponseOnConjunctionAndCutDatabaseWorks() {
        val nextStates = execute(SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase).toList()

        val interestingStates = nextStates.filterIsInstance<StateEnd.True>()
                .filter { it.solveRequest.equalSignatureAndArgs(SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase) }
        assertEquals(2, interestingStates.count())
        Scope.of(*SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase.arguments.map { it as Var }.toTypedArray()).where {
            assertEquals(
                    listOf(
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("a1")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("b1"))
                    ),
                    interestingStates.map { it.answerSubstitution }
            )
        }
    }

    @Test
    fun threeResponseOnCutAndConjunctionDatabase() {
        val nextStates = execute(SolverTestUtils.threeResponseOnCutAndConjunctionDatabase).toList()

        val interestingStates = nextStates.filterIsInstance<StateEnd.True>()
                .filter { it.solveRequest.equalSignatureAndArgs(SolverTestUtils.threeResponseOnCutAndConjunctionDatabase) }
        assertEquals(3, interestingStates.count())
        Scope.of(*SolverTestUtils.threeResponseOnCutAndConjunctionDatabase.arguments.map { it as Var }.toTypedArray()).where {
            assertEquals(
                    listOf(
                            Substitution.of(varOf("X") to numOf(2)),
                            Substitution.of(varOf("X") to numOf(4)),
                            Substitution.of(varOf("X") to numOf(6))
                    ),
                    interestingStates.map { it.answerSubstitution }
            )
        }
    }
}
