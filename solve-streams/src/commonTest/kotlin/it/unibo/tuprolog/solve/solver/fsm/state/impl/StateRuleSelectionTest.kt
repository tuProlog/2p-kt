package it.unibo.tuprolog.solve.solver.fsm.state.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.state.FinalState
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateRuleSelectionUtils.createRequest
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateRuleSelectionUtils.queryToMultipleMatchesDatabaseAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateRuleSelectionUtils.queryToNoMatchesDatabaseMap
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateRuleSelectionUtils.queryToOneMatchFactDatabaseAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateRuleSelectionUtils.queryToOneMatchRuleDatabaseAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateUtils.assertCorrectQueryAndSubstitution
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateUtils.assertOnlyOneNextState
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateUtils.assertOverFilteredStateInstances
import it.unibo.tuprolog.solve.solver.fsm.state.impl.testutils.StateUtils.assertOverState
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Test class for [StateRuleSelection]
 *
 * @author Enrico
 */
internal class StateRuleSelectionTest {

    private val theQueryVariable = Var.of("V")

    /** A struct query in the form `f(V)` */
    private val theQuery = Struct.of("f", theQueryVariable)

    /** A Solve.Request with three databases and three different facts, to test how they should be used in searching */
    private val threeDBSolveRequest = Solve.Request(theQuery.extractSignature(), theQuery.argsList,
            ExecutionContextImpl(
                    libraries = Libraries(Library.of(
                            alias = "testLib",
                            theory = ClauseDatabase.of({ factOf(structOf("f", atomOf("a"))) })
                    )),
                    staticKB = ClauseDatabase.of({ factOf(structOf("f", atomOf("b"))) }),
                    dynamicKB = ClauseDatabase.of({ factOf(structOf("f", atomOf("c"))) })
            ))

    @Test
    fun noMatchingRulesFoundMakeItGoIntoFalseState() {
        queryToNoMatchesDatabaseMap.forEach { (queryStruct, noMatchesDB) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, noMatchesDB)).behave()

            assertOnlyOneNextState<StateEnd.False>(nextStates)
        }
    }

    @Test
    fun oneMatchingRuleFoundUnifiesCorrectlyAndGivesSolution() {
        queryToOneMatchFactDatabaseAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave().toList()

            assertOverState<StateEnd.True>(nextStates.last()) {
                it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution)
            }
        }
    }

    @Test
    @Ignore // TODO: 05/11/2019 make returned substitution only contain variables of outer scope
    fun oneMatchingRuleFoundExecutesTheRuleBodyAndFindsSolutions() {
        queryToOneMatchRuleDatabaseAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
            val nextStates = StateRuleSelection(createRequest(queryStruct, oneMatchDB)).behave().toList()

            assertOverState<StateEnd>(nextStates.last()) {
                it.solve.solution.assertCorrectQueryAndSubstitution(queryStruct, expectedSubstitution)
            }
        }
    }

    @Test
    @Ignore // TODO: 05/11/2019 make returned substitution only contain variables of outer scope
    fun stateRuleSelectionFindsCorrectlyMultipleSolutions() {
        queryToMultipleMatchesDatabaseAndSubstitution.forEach { (queryStruct, oneMatchDB, expectedSubstitution) ->
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
            it.solve.solution.assertCorrectQueryAndSubstitution(theQuery, Substitution.of(theQueryVariable, Atom.of("a")))
        }
    }

    @Test
    fun stateRuleSelectionUsesCombinationOfStaticAndDynamicKBWhenLibraryTheoriesDoesntProvideMatches() {
        val dynamicAndStaticKBSolveRequest = with(threeDBSolveRequest) { copy(context = context.copy(libraries = Libraries())) }
        val correctSubstitutions = listOf(
                Substitution.of(theQueryVariable, Atom.of("b")),
                Substitution.of(theQueryVariable, Atom.of("c"))
        )

        val nextStates = StateRuleSelection(dynamicAndStaticKBSolveRequest).behave()

        assertOverFilteredStateInstances<FinalState>(nextStates) { index, finalState ->
            assertOverState<StateEnd>(finalState) {
                it.solve.solution.assertCorrectQueryAndSubstitution(theQuery, correctSubstitutions[index])
            }
        }
    }

}
