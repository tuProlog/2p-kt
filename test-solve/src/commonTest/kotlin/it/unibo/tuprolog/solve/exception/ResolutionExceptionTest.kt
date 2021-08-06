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
 * Test class for [ResolutionException]
 *
 * @author Enrico
 */
internal class ResolutionExceptionTest {

    private val exception = ResolutionException(aMessage, aCause, aContext)

    @Test
    fun constructorInsertsMessageIfOnlyCauseSpecified() {
        val exception = ResolutionException(aCause, aContext)

        assertEquals(aCause.toString(), exception.message)
    }

    @Test
    fun logicStackTraceAccessesContextCorrespondingField() {
        assertSame(emptyList(), exception.logicStackTrace)
    }

    @Test
    fun updateContextReturnsExceptionWithSameContentsButUpdatedContext() {
        val toBeTested = exception.updateContext(aDifferentContext)

        assertSameMessageCauseContext(aMessage, aCause, aDifferentContext, toBeTested)
    }
}
