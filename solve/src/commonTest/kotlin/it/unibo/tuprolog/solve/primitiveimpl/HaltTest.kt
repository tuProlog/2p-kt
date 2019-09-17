package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitiveimpl.testutils.HaltUtils
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.fail

/**
 * Test class
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
        try {
            Halt.primitive(HaltUtils.exposedHaltBehaviourRequest)
            fail("Exception should be thrown")
        } catch (e: HaltException) {
            assertSame(HaltUtils.exposedHaltBehaviourRequest.context, e.context)
        }
    }
}
