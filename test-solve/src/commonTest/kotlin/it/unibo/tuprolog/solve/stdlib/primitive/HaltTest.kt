package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.createSolveRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Halt]
 *
 * @author Enrico
 */
internal class HaltTest {
    private val haltSolveRequest = createSolveRequest(Atom.of("halt"))

    @Test
    fun haltPrimitiveThrowsHaltException() {
        assertFailsWith<HaltException> { Halt.implementation.solve(haltSolveRequest) }
    }

    @Test
    fun haltPrimitiveExceptionContainsCorrectContext() {
        assertOverFailure<ResolutionException>({ Halt.implementation.solve(haltSolveRequest) }) {
            assertEquals(haltSolveRequest.context, it.context)
        }
    }
}
