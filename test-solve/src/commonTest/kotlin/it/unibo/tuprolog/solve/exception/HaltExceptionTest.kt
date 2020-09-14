package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.anExitStatus
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.assertSameMessageCauseContextStatus
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [HaltException]
 *
 * @author Enrico
 */
internal class HaltExceptionTest {

    private val exception = HaltException(aMessage, aCause, aContext, anExitStatus)

    @Test
    fun holdsInsertedData() {
        assertSameMessageCauseContextStatus(aMessage, aCause, aContext, anExitStatus, exception)
    }

    @Test
    fun constructorInsertsMessageIfOnlyCauseSpecified() {
        val exception = HaltException(aCause, aContext, anExitStatus)

        assertEquals(aCause.toString(), exception.message)
    }

    @Test
    fun updateContextReturnsExceptionWithSameContentsButUpdatedContext() {
        val toBeTested = exception.updateContext(aDifferentContext)

        assertSameMessageCauseContextStatus(aMessage, aCause, aDifferentContext, anExitStatus, toBeTested)
    }
}
