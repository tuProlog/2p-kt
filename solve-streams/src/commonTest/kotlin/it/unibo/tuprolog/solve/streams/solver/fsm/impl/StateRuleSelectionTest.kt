package it.unibo.tuprolog.solve.streams.solver.fsm.impl

import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.FinalState
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateRuleSelectionUtils.createRequest
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToMultipleMatchesTheoryAndSubstitution
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToNoMatchesTheoryMap
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToOneMatchFactTheoryAndSubstitution
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateRuleSelectionUtils.queryToOneMatchRuleTheoryAndSubstitution
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateUtils.assertCorrectQueryAndSubstitution
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateUtils.assertOverFilteredStateInstances
import it.unibo.tuprolog.solve.streams.solver.fsm.impl.testutils.StateUtils.assertOverState
import kotlin.test.Test

/**
 * Test class for [StateRuleSelection]
 *
 * @author Enrico
 */
internal class StateRuleSelectionTest {

    private val theQueryVariable = Var.of("V")

    /** A struct query in the form `f(V)` */
    private val theQuery = logicProgramming { "f"(theQueryVariable) }

    /** A Solve.Request with three databases and three different facts, to test how they should be used/combined in searching */
    private val threeDBSolveRequest = Solve.Request(
        theQuery.extractSignature(),
        theQuery.args,
        StreamsExecutionContext(
            libraries = Runtime.of(
                Library.of(
                    alias = "testLib",
                    clauses = logicProgramming { theory({ "f"("a") }) }
                )
            ),
            staticKb = logicProgramming { theory({ "f"("b") }) },
            dynamicKb = logicProgramming { theory({ "f"("c") }) }
        )
    )

    @Test
    fun noMatchingRulesFoundMakeItGoIntoFalseState() {
        queryToNoMatchesTheoryMap.forEach { (queryStruct, noMatchesDB) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, noMatchesDB)).behave()

            assertOnlyOneNextState<StateEnd.False>(nextStates)
        }
    }

    @Test
    fun oneMatchingRuleFoundUnifiesCorrectlyAndGivesSolution() {
        queryToOneMatchFactTheoryAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave().toList()

            assertOverState<StateEnd.True>(nextStates.last()) {
                it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution)
            }
        }
    }

    @Test
    fun oneMatchingRuleFoundExecutesTheRuleBodyAndFindsSolutions() {
        queryToOneMatchRuleTheoryAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave().toList()

            assertOverState<StateEnd>(nextStates.last()) {
                it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution)
            }
        }
    }

    @Test
    fun stateRuleSelectionFindsCorrectlyMultipleSolutions() {
        queryToMultipleMatchesTheoryAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave()

            assertOverFilteredStateInstances<FinalState>(nextStates) { index, finalState ->
                assertOverState<StateEnd>(finalState) {
                    it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution[index])
                }
            }
        }
    }

    @Test
    fun stateRuleSelectionUsesFirstlyLibraryTheoryIfMatchesFoundAndNotOthers() {
        val nextStates = StateRuleSelection(threeDBSolveRequest).behave().toList()

        assertOverState<StateEnd.True>(nextStates.last()) {
            it.solve.solution.assertCorrectQueryAndSubstitution(theQuery, logicProgramming { theQueryVariable to "a" })
        }
    }

    @Test
    fun stateRuleSelectionUsesCombinationOfStaticAndDynamicKBWhenLibraryTheoriesDoesntProvideMatches() {
        val dynamicAndStaticKBSolveRequest =
            with(threeDBSolveRequest) { copy(context = context.copy(libraries = Runtime.empty())) }
        val correctSubstitutions = logicProgramming {
            ktListOf(
                theQueryVariable to "b",
                theQueryVariable to "c"
            )
        }

        val nextStates = StateRuleSelection(dynamicAndStaticKBSolveRequest).behave()

        assertOverFilteredStateInstances<FinalState>(nextStates) { index, finalState ->
            assertOverState<StateEnd>(finalState) {
                it.solve.solution.assertCorrectQueryAndSubstitution(theQuery, correctSubstitutions[index])
            }
        }
    }
}
