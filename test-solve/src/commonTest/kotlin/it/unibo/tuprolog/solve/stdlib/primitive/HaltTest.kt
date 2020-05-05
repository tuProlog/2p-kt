package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.createSolveRequest
import it.unibo.tuprolog.solve.assertOverFailure
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
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
        assertFailsWith<HaltException> { Halt.wrappedImplementation(haltSolveRequest) }
    }

    @Test
    fun haltPrimitiveExceptionContainsCorrectContext() {
        assertOverFailure<TuPrologRuntimeException>({ Halt.wrappedImplementation(haltSolveRequest) }) {
            assertEquals(haltSolveRequest.context, it.context)
        }
    }
}
