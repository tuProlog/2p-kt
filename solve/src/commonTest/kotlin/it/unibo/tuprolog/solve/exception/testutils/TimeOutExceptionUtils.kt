package it.unibo.tuprolog.solve.exception.testutils

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.exception.TimeOutException
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [TimeOutException]
 *
 * @author Enrico
 */
internal object TimeOutExceptionUtils {

    internal const val aMessage = TuPrologRuntimeExceptionUtils.aMessage
    internal val aCause = TuPrologRuntimeExceptionUtils.aCause
    internal val aContext = TuPrologRuntimeExceptionUtils.aContext
    internal const val anExceededTimeDuration = TimeDuration.MAX_VALUE

    internal val aDifferentContext = TuPrologRuntimeExceptionUtils.aDifferentContext

    /** Utility function to check if exception contains same expected values */
    fun assertSameMessageCauseContextDuration(
            expectedMessage: String?,
            expectedCause: Throwable?,
            expectedContext: ExecutionContext,
            expectedExceededTimeDuration: TimeDuration,
            actualException: TimeOutException
    ) {
        TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext(expectedMessage, expectedCause, expectedContext, actualException)
        assertEquals(expectedExceededTimeDuration, actualException.exceededDuration)
    }
}
