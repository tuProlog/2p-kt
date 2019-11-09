package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.getSideEffectManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

/**
 * Test class for [Cut]
 *
 * @author Enrico
 */
internal class CutTest {

    private val cutPrimitiveSignature = Signature("!", 0)

    private fun cutRequest(context: ExecutionContext = ExecutionContextImpl()) =
            Solve.Request(cutPrimitiveSignature, emptyList(), context)

    @Test
    fun cutPrimitiveReturnsAlwaysYesResponseWithRequestSubstitution() {
        val substitution = Substitution.of("A", Truth.`true`())
        val context = ExecutionContextImpl(substitution = substitution)
        val solutions = Cut.wrappedImplementation(cutRequest(context))

        assertEquals(1, solutions.count())
        with(solutions.single().solution) {
            assertTrue { this is Solution.Yes }
            assertEquals(cutPrimitiveSignature.withArgs(emptyList()), this.query)
            assertEquals(substitution, this.substitution)
        }
    }

    @Test
    fun cutPrimitiveModifiesSideEffectManager() {
        val request = cutRequest()
        val response = Cut.wrappedImplementation(request).single()

        assertNotSame(request.context.getSideEffectManager(), response.sideEffectManager)
    }
}
