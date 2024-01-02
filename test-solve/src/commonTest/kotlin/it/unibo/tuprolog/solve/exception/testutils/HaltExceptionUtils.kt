package it.unibo.tuprolog.solve.exception.testutils

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.HaltException
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [HaltException]
 *
 * @author Enrico
 */
@Suppress("ConstPropertyName", "ktlint:standard:property-naming")
internal object HaltExceptionUtils {
    internal const val aMessage = TuPrologRuntimeExceptionUtils.aMessage
    internal val aCause = TuPrologRuntimeExceptionUtils.aCause
    internal val aContext = TuPrologRuntimeExceptionUtils.aContext
    internal const val anExitStatus = 0

    internal val aDifferentContext = TuPrologRuntimeExceptionUtils.aDifferentContext

    /** Utility function to check if exception contains same expected values */
    internal fun assertSameMessageCauseContextStatus(
        expectedMessage: String?,
        expectedCause: Throwable?,
        expectedContext: ExecutionContext,
        expectedExitStatus: Int,
        actualException: HaltException,
    ) {
        TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext(
            expectedMessage,
            expectedCause,
            expectedContext,
            actualException,
        )
        assertEquals(expectedExitStatus, actualException.exitStatus)
    }
}
