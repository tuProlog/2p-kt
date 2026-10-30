package it.unibo.tuprolog.solve.exception

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Test class for [ResolutionException]
 *
 * @author Enrico
 */
class ResolutionExceptionSpec :
    FunSpec({
        val exception = ResolutionException(aMessage, aCause, aContext)

        test("constructor-inserts-message-if-only-cause-specified") {
            val exception = ResolutionException(aCause, aContext)

            assertEquals(aCause.toString(), exception.message)
        }

        test("logic-stack-trace-accesses-context-corresponding-field") {
            assertSame(emptyList(), exception.logicStackTrace)
        }

        test("update-context-returns-exception-with-same-contents-but-updated-context") {
            val toBeTested = exception.updateContext(aDifferentContext)

            assertSameMessageCauseContext(aMessage, aCause, aDifferentContext, toBeTested)
        }
    })
