package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [TuPrologRuntimeException]
 *
 * @author Enrico
 */
internal class TuPrologRuntimeExceptionTest {

    private val exception = TuPrologRuntimeException(aMessage, aCause, aContext)

    @Test
    fun constructorInsertsMessageIfOnlyCauseSpecified() {
        val exception = TuPrologRuntimeException(aCause, aContext)

        assertEquals(aCause.toString(), exception.message)
    }

    @Test
    fun prologStackTraceAccessesContextCorrespondingField() {
        assertSame(emptyList(), exception.prologStackTrace)
    }

    @Test
    fun updateContextReturnsExceptionWithSameContentsButUpdatedContext() {
        val toBeTested = exception.updateContext(aDifferentContext)

        assertSameMessageCauseContext(aMessage, aCause, aDifferentContext, toBeTested)
    }
}
