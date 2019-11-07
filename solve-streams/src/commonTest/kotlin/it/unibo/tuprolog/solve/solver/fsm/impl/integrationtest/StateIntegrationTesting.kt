package it.unibo.tuprolog.solve.solver.fsm.impl.integrationtest

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabase
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.FinalState
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.fsm.impl.StateEnd
import it.unibo.tuprolog.solve.solver.fsm.impl.StateGoalEvaluation
import it.unibo.tuprolog.solve.solver.fsm.impl.StateInit
import it.unibo.tuprolog.solve.solver.fsm.impl.StateRuleSelection
import it.unibo.tuprolog.solve.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for testing interactions between states and state machine correct evolution
 *
 * @author Enrico
 */
internal class StateIntegrationTesting { // TODO: 05/11/2019 maybe before refactoring this, a refactor in where and how general tests are held/made is necessary to not write thousand [ClauseDatabase]s

    /** Shorthand function to execute a solveRequest */
    private fun Solve.Request<ExecutionContextImpl>.executeFSM(): Sequence<State> =
            StateMachineExecutor.execute(StateInit(this))

    @Test
    fun trueSolveRequestWorks() {
        val nextStates = createSolveRequest(Atom.of("true")).executeFSM()

        assertOnlyOneNextState<StateEnd.True>(nextStates)
    }

    // TODO: 07/11/2019 still to review tests below this comment

    @Test
    fun nonPresentClause() {
        val nextStates = createSolveRequest(Atom.of("ciao")).executeFSM().toList()

        assertEquals(3, nextStates.count())
        // TODO: 05/11/2019 create a method to programmatically check for correct state sequence
        assertTrue { nextStates.component1() is StateGoalEvaluation }
        assertTrue { nextStates.component2() is StateRuleSelection }
        assertTrue { nextStates.component3() is StateEnd.False }
    }

    @Test
    fun requiringSimpleQueryWithVariableInstantiationWorks() {
        val nextStates = createSolveRequest(Struct.of("f", Var.of("A")), simpleFactDatabase).executeFSM().toList()

        assertEquals(5, nextStates.count())
        assertTrue { nextStates[0] is StateGoalEvaluation }
        assertTrue { nextStates[1] is StateRuleSelection }
        assertTrue { nextStates[2] is StateInit }
        assertTrue { nextStates[3] is StateEnd.True }
        assertTrue { nextStates[4] is StateEnd.True }

        val answerSubstitution = (nextStates.last() as FinalState).solve.solution.substitution
        val scope = Scope.of(*answerSubstitution.keys.toTypedArray())

        assertEquals(Atom.of("a"), answerSubstitution[scope.varOf("A")])
        assertEquals(1, answerSubstitution.count())
    }

    @Test
    fun requiringQueryWithMultipleInstantiationsWorks() {
        val hSolveRequest = createSolveRequest(Struct.of("h", Var.of("A")), simpleFactDatabase)

        val nextStates = hSolveRequest.executeFSM().toList()

        assertEquals(11, nextStates.count())

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(6, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == hSolveRequest.query }
        assertEquals(3, interestingStates.count())

        val answerSubstitution = interestingStates.map { it.solve.solution.substitution }

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
        val nextStates = SolverTestUtils.oneResponseBecauseOfCut.executeFSM().toList()

        assertEquals(6, nextStates.count())

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(2, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.oneResponseBecauseOfCut.query }
        assertEquals(1, interestingStates.count())
        assertEquals(Atom.of("only"), interestingStates.single().solve.solution.substitution.values.single())
    }

    @Test
    fun twoAlternativesBeforeCutExecutionShouldBeReturned() {
        val nextStates = SolverTestUtils.twoResponseBecauseOfCut.executeFSM().toList()

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(4, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.twoResponseBecauseOfCut.query }
        assertEquals(2, interestingStates.count())
        assertEquals(
                listOf(Atom.of("a"), Atom.of("only")),
                interestingStates.map { it.solve.solution.substitution.values.single() }
        )
    }

    @Test
    fun threeAlternativesBecauseOfInternalCutExecution() {
        val nextStates = SolverTestUtils.threeResponseBecauseOfCut.executeFSM().toList()

        val trueEndStates = nextStates.filterIsInstance<StateEnd.True>()
        assertEquals(9, trueEndStates.count())
        assertEquals(trueEndStates.count(), nextStates.filterIsInstance<StateEnd>().count())

        val interestingStates = trueEndStates.filter { it.solve.solution.query == SolverTestUtils.threeResponseBecauseOfCut.query }
        assertEquals(3, interestingStates.count())
        assertEquals(
                listOf(Atom.of("a"), Atom.of("c"), Atom.of("d")),
                interestingStates.map { it.solve.solution.substitution.values.single() }
        )
    }

    @Test
    fun twoResponseOnConjunctionAndCutDatabaseWorks() {
        val nextStates = SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase.executeFSM().toList()

        val interestingStates = nextStates.filterIsInstance<StateEnd.True>()
                .filter { it.solve.solution.query == SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase.query }
        assertEquals(2, interestingStates.count())
        Scope.of(*SolverTestUtils.twoResponseOnConjunctionAndMiddleCutDatabase.arguments.map { it as Var }.toTypedArray()).where {
            assertEquals(
                    listOf(
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("a1")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("b1"))
                    ),
                    interestingStates.map { it.solve.solution.substitution }
            )
        }
    }

    @Test
    fun threeResponseOnCutAndConjunctionDatabase() {
        val nextStates = SolverTestUtils.threeResponseOnCutAndConjunctionDatabase.executeFSM().toList()

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
                    interestingStates.map { it.solve.solution.substitution }
            )
        }
    }
}
