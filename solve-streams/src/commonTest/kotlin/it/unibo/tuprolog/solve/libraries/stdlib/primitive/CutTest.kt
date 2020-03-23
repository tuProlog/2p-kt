package it.unibo.tuprolog.solve.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.libraries.stdlib.primitive.testutils.PrimitiveUtils.assertOnlyOneSolution
import it.unibo.tuprolog.solve.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.getSideEffectManager
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

    private fun cutRequest(context: ExecutionContext = ExecutionContextImpl()) =
        Solve.Request(cutPrimitiveSignature, emptyList(), context)

    @Test
    fun cutPrimitiveReturnsAlwaysYesResponseWithRequestSubstitution() {
        val unchangedSubstitution = Substitution.of("A", Truth.ofTrue())
        val context = ExecutionContextImpl(substitution = unchangedSubstitution)
        val solutions = Cut.wrappedImplementation(cutRequest(context))

        assertOnlyOneSolution(Atom.of("!").yes(unchangedSubstitution), solutions)
    }

    @Test
    fun cutPrimitiveModifiesSideEffectManager() {
        val request = cutRequest()
        val response = Cut.wrappedImplementation(request).single()

        assertNotSame(request.context.getSideEffectManager(), response.sideEffectManager)
    }
}
