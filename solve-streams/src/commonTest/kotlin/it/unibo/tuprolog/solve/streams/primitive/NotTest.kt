package it.unibo.tuprolog.solve.streams.primitive

import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Call
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Not
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Test class for [Not]
 *
 * @author Enrico
 */
internal class NotTest {
    @Test
    fun notRevertsProvidedNoResponse() {
        val query = logicProgramming { "\\+"(false) }
        val solutions =
            Not.implementation
                .solve(
                    createSolveRequest(query, primitives = mapOf(Not.descriptionPair, Call.descriptionPair)),
                ).map { it.solution }
                .asIterable()

        assertSolutionEquals(listOf(query.yes()), solutions)
    }

    @Test
    fun notRevertsProvidedYesResponse() {
        val query = logicProgramming { "\\+"(true) }
        val solutions =
            Not.implementation
                .solve(
                    createSolveRequest(query, primitives = mapOf(Not.descriptionPair, Call.descriptionPair)),
                ).map { it.solution }
                .asIterable()

        assertSolutionEquals(listOf(query.no()), solutions)
    }

    @Test
    fun notReturnsOnlyOneFailResponseIfMoreTrueOnes() {
        val query = logicProgramming { "\\+"("a") }
        val solutions =
            Not.implementation
                .solve(
                    createSolveRequest(
                        query,
                        primitives = mapOf(Not.descriptionPair, Call.descriptionPair),
                        database = LogicProgrammingScope.of().theory({ "a" }, { "a" }),
                    ),
                ).map { it.solution }
                .asIterable()

        assertSolutionEquals(listOf(query.no()), solutions)
    }

    @Test
    @Ignore
    fun notReturnsAsIsAnHaltSolution() {
        val query = logicProgramming { "\\+"(1) }
        val solutions =
            Not.implementation
                .solve(
                    createSolveRequest(
                        query,
                        primitives = mapOf(Not.descriptionPair, Call.descriptionPair, Throw.descriptionPair),
                    ),
                ).map { it.solution }
                .asIterable()

        assertSolutionEquals(
            listOf(
                query.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        Not.signature,
                        TypeError.Expected.CALLABLE,
                        logicProgramming { numOf(1) },
                    ),
                ),
            ),
            solutions,
        )
    }
}
