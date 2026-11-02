package it.unibo.tuprolog.solve.exception

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.anExitStatus
import it.unibo.tuprolog.solve.exception.testutils.HaltExceptionUtils.assertSameMessageCauseContextStatus

/**
 * Test class for [HaltException]
 *
 * @author Enrico
 */
class HaltExceptionSpec :
    FunSpec({
        val exception = HaltException(anExitStatus, aMessage, aCause, aContext)

        test("holds-inserted-data") {
            assertSameMessageCauseContextStatus(aMessage, aCause, aContext, anExitStatus, exception)
        }

        test("constructor-inserts-message-if-only-cause-specified") {
            val exception = HaltException(anExitStatus, aCause, aContext)

            exception.message shouldBe aCause.toString()
        }

        test("update-context-returns-exception-with-same-contents-but-updated-context") {
            val toBeTested = exception.updateContext(aDifferentContext)

            assertSameMessageCauseContextStatus(aMessage, aCause, aDifferentContext, anExitStatus, toBeTested)
        }
    })
