package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitiveimpl.testutils.HaltUtils
import it.unibo.tuprolog.solve.primitiveimpl.testutils.PrimitivesUtils.assertRequestContextEqualToThrownErrorOne
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * Test class for [Halt]
 *
 * @author Enrico
 */
internal class HaltTest {

    @Test
    fun haltPrimitiveThrowsHaltException() {
        assertFailsWith<HaltException> { Halt.primitive(HaltUtils.exposedHaltBehaviourRequest) }
    }

    @Test
    fun haltPrimitiveExceptionContainsCorrectContext() {
        assertRequestContextEqualToThrownErrorOne(HaltUtils.exposedHaltBehaviourRequest, Halt)
    }
}
