package it.unibo.tuprolog.solve.exception.testutils

import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.TimeOutException
import kotlin.time.Duration

/**
 * Utils singleton to help testing [TimeOutException]
 *
 * @author Enrico
 */
@Suppress("ConstPropertyName", "ktlint:standard:property-naming")
internal object TimeOutExceptionUtils {
    internal const val aMessage = TuPrologRuntimeExceptionUtils.aMessage
    internal val aCause = TuPrologRuntimeExceptionUtils.aCause
    internal val aContext = TuPrologRuntimeExceptionUtils.aContext
    internal val anExceededTimeDuration = Duration.INFINITE

    internal val aDifferentContext = TuPrologRuntimeExceptionUtils.aDifferentContext

    /** Utility function to check if exception contains same expected values */
    internal fun assertSameMessageCauseContextDuration(
        expectedMessage: String?,
        expectedCause: Throwable?,
        expectedContext: ExecutionContext,
        expectedExceededTimeDuration: Duration,
        actualException: TimeOutException,
    ) {
        TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext(
            expectedMessage,
            expectedCause,
            expectedContext,
            actualException,
        )
        actualException.exceededDuration shouldBe expectedExceededTimeDuration
    }
}
