package it.unibo.tuprolog.solve.exception.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

/**
 * Utils singleton to help testing [ResolutionException]
 *
 * @author Enrico
 */
internal object TuPrologRuntimeExceptionUtils {

    internal const val aMessage = "ciao"
    internal val aCause = IllegalArgumentException()

    /** A context with emptySequence [ExecutionContext.prologStackTrace] field */
    internal val aContext = object : ExecutionContext by DummyInstances.executionContext {
        override val prologStackTrace: List<Struct> = emptyList()
    }

    /** Different instance from [aContext] with same behaviour */
    internal val aDifferentContext = object : ExecutionContext by aContext {}
        .also { assertNotEquals(it, aContext) }

    /** Utility function to check if exception contains same expected values */
    internal fun assertSameMessageCauseContext(
        expectedMessage: String?,
        expectedCause: Throwable?,
        expectedContext: ExecutionContext,
        actualException: ResolutionException
    ) {
        assertSame(expectedMessage, actualException.message)
        assertSame(expectedCause, actualException.cause)
        assertSame(expectedContext, actualException.context)
    }
}
