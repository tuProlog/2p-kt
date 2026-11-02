package it.unibo.tuprolog.solve.exception

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext

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

            exception.message shouldBe aCause.toString()
        }

        test("logic-stack-trace-accesses-context-corresponding-field") {
            exception.logicStackTrace shouldBeSameInstanceAs emptyList<Nothing>()
        }

        test("update-context-returns-exception-with-same-contents-but-updated-context") {
            val toBeTested = exception.updateContext(aDifferentContext)

            assertSameMessageCauseContext(aMessage, aCause, aDifferentContext, toBeTested)
        }
    })
