package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.Library
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Conjunction]
 *
 * @author Enrico
 */
internal class ConjunctionTest {

    /** Utility function to create solveRequest of provided query with Conjunction library primitive */
    private fun createConjunctionSolveRequest(query: Struct) = Solve.Request(
            Signature.fromIndicator(query.indicator)!!,
            query.argsList,
            DummyInstances.executionContext.copy(libraries = Libraries(Library.of(
                    alias = "Test",
                    theory = StateRuleSelectionUtils.multipleMatchesDatabase,
                    primitives = mapOf(with(Conjunction) { signature to primitive })
            )))
    )

    private val threeResponseQuery = Struct.of("f", Var.anonymous())
    private val trueAndTrueSolveRequest = createConjunctionSolveRequest(Tuple.of(Truth.`true`(), Truth.`true`()))
    private val multipleSolutionRequest = createConjunctionSolveRequest(Tuple.of(threeResponseQuery, Truth.`true`()))

    private val failedRequests by lazy {
        listOf(
                createConjunctionSolveRequest(Tuple.of(Truth.fail(), threeResponseQuery)),
                createConjunctionSolveRequest(Tuple.of(threeResponseQuery, Truth.fail())),
                createConjunctionSolveRequest(Tuple.of(threeResponseQuery, threeResponseQuery, Truth.fail()))
        )
    }

    @Test
    fun conjunctionOfTrueReturnsTrue() {
        val responses = Conjunction.primitive(trueAndTrueSolveRequest)

        assertEquals(1, responses.count())
        assertTrue { responses.single().solution is Solution.Yes }
    }

    @Test
    fun conjunctionReturnsCorrectlyMultipleSolutionsOnLeftSide() {
        val responses = Conjunction.primitive(multipleSolutionRequest)

        assertEquals(3, responses.count())
        assertTrue { responses.first().solution is Solution.No }
        responses.drop(1).forEach { assertTrue { it.solution is Solution.Yes } }
    }

    @Test
    fun conjunctionFailsIfSomePredicateIsNotTrue() {
        failedRequests.forEach { request ->
            val responses = Conjunction.primitive(request)

            assertTrue { responses.map(Solve.Response::solution).all { it is Solution.No } }
        }
    }

    // TODO: 01/09/2019 more deeper testing
}
