package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils.multipleMatchesDatabase
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [Conjunction]
 *
 * @author Enrico
 */
internal class ConjunctionTest {

    private val fAnonymousQuery = Struct.of("f", Var.anonymous())
    private val trueAndTrueSolveRequest = createSolveRequest(Tuple.of(Truth.`true`(), Truth.`true`()), multipleMatchesDatabase)
    private val multipleSolutionRequest = createSolveRequest(Tuple.of(fAnonymousQuery, Truth.`true`()), multipleMatchesDatabase)

    private val failedRequests by lazy {
        listOf(
                createSolveRequest(Tuple.of(Truth.fail(), fAnonymousQuery), multipleMatchesDatabase),
                createSolveRequest(Tuple.of(fAnonymousQuery, Truth.fail()), multipleMatchesDatabase),
                createSolveRequest(Tuple.of(fAnonymousQuery, fAnonymousQuery, Truth.fail()), multipleMatchesDatabase)
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
