package it.unibo.tuprolog.solve.exception.testutils

import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.ResolutionException

/**
 * Utils singleton to help testing [ResolutionException]
 *
 * @author Enrico
 */
@Suppress("ConstPropertyName", "ktlint:standard:property-naming")
internal object TuPrologRuntimeExceptionUtils {
    internal const val aMessage = "ciao"
    internal val aCause = IllegalArgumentException()

    /** A context with emptySequence [ExecutionContext.logicStackTrace] field */
    internal val aContext =
        object : ExecutionContext by DummyInstances.executionContext {
            override val logicStackTrace: List<Struct> = emptyList()
        }

    /** Different instance from [aContext] with same behaviour */
    internal val aDifferentContext =
        object : ExecutionContext by aContext {}
            .also { aContext shouldNotBe it }

    /** Utility function to check if exception contains same expected values */
    internal fun assertSameMessageCauseContext(
        expectedMessage: String?,
        expectedCause: Throwable?,
        expectedContext: ExecutionContext,
        actualException: ResolutionException,
    ) {
        actualException.message shouldBeSameInstanceAs expectedMessage
        actualException.cause shouldBeSameInstanceAs expectedCause
        actualException.context shouldBeSameInstanceAs expectedContext
    }
}
