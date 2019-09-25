package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.testutils.StateRuleSelectionUtils.multipleMatchesDatabase
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
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

    @Test
    fun conjunctionReturnsResponseContextWithOnlySubstitutionEnriched() {
        val aScope = Scope.of("L", "R")
        val firstSubstitution = Substitution.of(aScope.varOf("L"), Atom.of("first"))
        val secondSubstitution = Substitution.of(aScope.varOf("R"), Atom.of("second"))
        val aToBeCutContext = DummyInstances.executionContext.copy()
        val leftPrimitive = object : PrimitiveWrapper(Signature("left", 0)) {
            override val uncheckedImplementation: Primitive = {
                it as Solve.Request<ExecutionContextImpl>

                sequenceOf(
                        Solve.Response(
                                Solution.Yes(it.signature, it.arguments, (it.context.substitution + firstSubstitution).asUnifier()),
                                context = it.context.copy(
                                        substitution = (it.context.substitution + firstSubstitution).asUnifier(),
                                        toCutContextsParent = sequenceOf(aToBeCutContext)
                                )
                        )
                )
            }
        }
        val rightPrimitive = object : PrimitiveWrapper(Signature("right", 0)) {
            override val uncheckedImplementation: Primitive = {
                it as Solve.Request<ExecutionContextImpl>
                sequenceOf(
                        Solve.Response(
                                Solution.Yes(it.signature, it.arguments, (it.context.substitution + secondSubstitution).asUnifier()),
                                context = it.context.copy(substitution = (it.context.substitution + secondSubstitution).asUnifier())
                        )
                )
            }
        }
        val request = createSolveRequest(
                Tuple.of(Atom.of("left"), Atom.of("right")),
                primitives = mapOf(*listOf(Conjunction, leftPrimitive, rightPrimitive).map { it.descriptionPair }.toTypedArray())
        )

        val responses = Conjunction.primitive(request).toList()
        assertEquals(1, responses.count())
        assertEquals(Substitution.of(firstSubstitution, secondSubstitution), responses.single().context!!.substitution)
        assertSame(aToBeCutContext, responses.single().context!!.toCutContextsParent.single())
    }
}
