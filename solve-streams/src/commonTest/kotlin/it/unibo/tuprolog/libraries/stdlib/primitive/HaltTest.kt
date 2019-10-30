package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.HaltUtils
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

    @Test
    fun haltPrimitiveThrowsHaltException() {
        assertFailsWith<HaltException> { Halt.wrappedImplementation(HaltUtils.exposedHaltBehaviourRequest) }
    }

    @Test
    fun haltPrimitiveExceptionContainsCorrectContext() {
        assertOverFailure<TuPrologRuntimeException>({ Halt.wrappedImplementation(HaltUtils.exposedHaltBehaviourRequest) }) {
            assertEquals(HaltUtils.exposedHaltBehaviourRequest.context, it.context)
        }
    }
}
