package it.unibo.tuprolog.solve.streams.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.primitive.testutils.PrimitiveUtils.assertOnlyOneSolution
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.getSideEffectManager
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test
import kotlin.test.assertNotSame

/**
 * Test class for [Cut]
 *
 * @author Enrico
 */
internal class CutTest {
    private val cutPrimitiveSignature = Signature("!", 0)

    private fun cutRequest(context: ExecutionContext = StreamsExecutionContext()) =
        Solve.Request(cutPrimitiveSignature, emptyList(), context)

    @Test
    fun cutPrimitiveReturnsAlwaysYesResponseWithRequestSubstitution() {
        val unchangedSubstitution = Substitution.of("A", Truth.TRUE)
        val context = StreamsExecutionContext(substitution = unchangedSubstitution)
        val solutions = Cut.implementation.solve(cutRequest(context))

        assertOnlyOneSolution(Atom.of("!").yes(unchangedSubstitution), solutions)
    }

    @Test
    fun cutPrimitiveModifiesSideEffectManager() {
        val request = cutRequest()
        val response = Cut.implementation.solve(request).single()

        assertNotSame(request.context.getSideEffectManager(), response.sideEffectManager)
    }
}
