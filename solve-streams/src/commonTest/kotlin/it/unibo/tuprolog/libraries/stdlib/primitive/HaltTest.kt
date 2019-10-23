package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.HaltUtils
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.PrimitivesUtils.assertRequestContextEqualToThrownErrorOne
import it.unibo.tuprolog.solve.exception.HaltException
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
