package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.statemachine.state.*
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateInitUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for testing interactions between states and state machine correct evolution
 *
 * @author Enrico
 */
internal class StateIntegrationTesting {

    /** Shorthand function to execute a solveRequest */
    private fun execute(solveRequest: Solve.Request<ExecutionContextImpl>): Sequence<State> =
            StateMachineExecutor.execute(StateInit(solveRequest, DummyInstances.executionStrategy))

    /** Utility function to compute answer substitution */
    // TODO soluzione provvisioria in attesa di rifattorizzare i test
    private fun StateEnd.True.answerSubstitution() =
            with(solve) { solution.substitution.filter { (`var`, _) -> `var` in solution.query.variables } }

    @Test
    fun trueSolveRequestWorks() {
        val nextStates = execute(StateInitUtils.trueSolveRequest).toList()

        assertEquals(1, nextStates.count())
        assertTrue { nextStates.component1() is StateEnd.True }
    }

    @Test
    fun nonPresentClause() {
        val nextStates = execute(StateInitUtils.failSolveRequest).toList()

        assertEquals(3, nextStates.count())
        assertTrue { nextStates.component1() is StateGoalEvaluation }
        assertTrue { nextStates.component2() is StateRuleSelection }
        assertTrue { nextStates.component3() is StateEnd.False }
    }

    @Test
    fun requiringSimpleQueryWithVariableInstantiationWorks() {
        val nextStates = execute(SolverTestUtils.oneResponseRequest).toList()

        assertEquals(5, nextStates.count())
        assertTrue { nextStates[0] is StateGoalEvaluation }
        assertTrue { nextStates[1] is StateRuleSelection }
        assertTrue { nextStates[2] is StateInit }
        assertTrue { nextStates[3] is StateEnd.True }
        assertTrue { nextStates[4] is StateEnd.True }

        val answerSubstitution = (nextStates[4] as StateEnd.True).answerSubstitution()
        val scope = Scope.of(*answerSubstitution.keys.toTypedArray())

        assertEquals(Atom.of("a"), answerSubstitution[scope.varOf("A")])
        assertEquals(1, answerSubstitution.count())
    }

    @Test
    fun requiringQueryWithMultipleInstantiationsWorks() {
        val nextStates = execute(SolverTestUtils.threeResponseRequest).toList()

        assertEquals(11, nextStates.count())

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(6, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.threeResponseRequest.query }
        assertEquals(3, interestingStates.count())

        val answerSubstitution = interestingStates.map { it.answerSubstitution() }

        val correctSubstitution = listOf("a", "b", "c").map(Atom.Companion::of)
        correctSubstitution.zip(answerSubstitution).forEach { (expectedSubstituent, substitution) ->
            val currentScope = Scope.of(*substitution.keys.toTypedArray())

            assertEquals(expectedSubstituent, substitution[currentScope.varOf("A")])
            assertEquals(1, substitution.count())
        }
    }

//    @Test
//    fun requiringQueryWithMultipleInstantiationsHasStateInternalsCorrect() {
//        val nextStates = execute(SolverTestUtils.threeResponseRequest).toList()
//        assertEquals(11, nextStates.count())
//
//        val subInitStates = nextStates.filterIsInstance<StateInit>()
//        assertEquals(3, subInitStates.count())
//        assertTrue { subInitStates.all { it.solve.context.isChoicePointChild } }
////        assertTrue { (nextStates - subInitStates).none { (it.solve.context as DeclarativeImplExecutionContext).isChoicePointChild } }
//
//        val subRuleSelectionStateContexts = nextStates.filterIsInstance<StateRuleSelection>().map { it.solve.context }
//        subRuleSelectionStateContexts.zip(subInitStates.map { it.solve.context }).forEach { (expected, actual) ->
//            assertSame(expected, actual.clauseScopedParents.first())
//        }
//
//        subRuleSelectionStateContexts.zip(subInitStates.map { it.solve.context }).forEach { (ruleSelectionContext, initContext) ->
//            assertSame(ruleSelectionContext, initContext.clauseScopedParents.first())
//        }
//    }

    @Test
    fun requiringQueryWithCutDoesntReturnOtherAlternatives() {
        val nextStates = execute(SolverTestUtils.oneResponseBecauseOfCut).toList()

        assertEquals(6, nextStates.count())

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(2, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.oneResponseBecauseOfCut.query }
        assertEquals(1, interestingStates.count())
        assertEquals(Atom.of("only"), interestingStates.single().answerSubstitution().values.single())
    }

    @Test
    fun twoAlternativesBeforeCutExecutionShouldBeReturned() {
        val nextStates = execute(SolverTestUtils.twoResponseBecauseOfCut).toList()

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(4, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.twoResponseBecauseOfCut.query }
        assertEquals(2, interestingStates.count())
        assertEquals(
                listOf(Atom.of("a"), Atom.of("only")),
                interestingStates.map { it.answerSubstitution().values.single() }
        )
    }

    @Test
    fun threeAlternativesBecauseOfInternalCutExecution() {
        val nextStates = execute(SolverTestUtils.threeResponseBecauseOfCut).toList()

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(9, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.threeResponseBecauseOfCut.query }
        assertEquals(3, interestingStates.count())
        assertEquals(
                listOf(Atom.of("a"), Atom.of("c"), Atom.of("d")),
                interestingStates.map { it.answerSubstitution().values.single() }
        )
    }

    @Test
    fun twoResponseOnConjunctionAndCutDatabaseWorks() {
        val nextStates = execute(SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase).toList()

        val interestingStates = nextStates.filterIsInstance<StateEnd.True>()
                .filter { it.solve.solution.query == SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase.query }
        assertEquals(2, interestingStates.count())
        Scope.of(*SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase.arguments.map { it as Var }.toTypedArray()).where {
            assertEquals(
                    listOf(
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("a1")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("b1"))
                    ),
                    interestingStates.map { it.answerSubstitution() }
            )
        }
    }

    @Test
    fun threeResponseOnCutAndConjunctionDatabase() {
        val nextStates = execute(SolverTestUtils.threeResponseOnCutAndConjunctionDatabase).toList()

        val interestingStates = nextStates.filterIsInstance<StateEnd.True>()
                .filter { it.solve.solution.query == SolverTestUtils.threeResponseOnCutAndConjunctionDatabase.query }
        assertEquals(3, interestingStates.count())
        Scope.of(*SolverTestUtils.threeResponseOnCutAndConjunctionDatabase.arguments.map { it as Var }.toTypedArray()).where {
            assertEquals(
                    listOf(
                            Substitution.of(varOf("X") to numOf(2)),
                            Substitution.of(varOf("X") to numOf(4)),
                            Substitution.of(varOf("X") to numOf(6))
                    ),
                    interestingStates.map { it.answerSubstitution() }
            )
        }
    }
}
