package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.anExceededTimeDuration
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.assertSameMessageCauseContextDuration
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [TimeOutException]
 *
 * @author Enrico
 */
internal class TimeOutExceptionTest {

    private val exception = TimeOutException(aMessage, aCause, aContext, anExceededTimeDuration)

    @Test
    fun holdsInsertedData() {
        assertSameMessageCauseContextDuration(aMessage, aCause, aContext, anExceededTimeDuration, exception)
    }

    @Test
    fun constructorInsertsMessageIfOnlyCauseSpecified() {
        val exception = TimeOutException(aCause, aContext, anExceededTimeDuration)

        assertEquals(aCause.toString(), exception.message)
    }

    @Test
    fun updateContextReturnsExceptionWithSameContentsButUpdatedContext() {
        val toBeTested = exception.updateContext(aDifferentContext)

        assertSameMessageCauseContextDuration(aMessage, aCause, aDifferentContext, anExceededTimeDuration, toBeTested)
    }
}
