package it.unibo.tuprolog.solve.exception

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.anExceededTimeDuration
import it.unibo.tuprolog.solve.exception.testutils.TimeOutExceptionUtils.assertSameMessageCauseContextDuration

/**
 * Test class for [TimeOutException]
 *
 * @author Enrico
 */
class TimeOutExceptionSpec :
    FunSpec({
        val exception = TimeOutException(aMessage, aCause, aContext, anExceededTimeDuration)

        test("holds-inserted-data") {
            assertSameMessageCauseContextDuration(aMessage, aCause, aContext, anExceededTimeDuration, exception)
        }

        test("constructor-inserts-message-if-only-cause-specified") {
            val exception = TimeOutException(aCause, aContext, anExceededTimeDuration)

            exception.message shouldBe aCause.toString()
        }

        test("update-context-returns-exception-with-same-contents-but-updated-context") {
            val toBeTested = exception.updateContext(aDifferentContext)

            assertSameMessageCauseContextDuration(
                aMessage,
                aCause,
                aDifferentContext,
                anExceededTimeDuration,
                toBeTested,
            )
        }
    })
