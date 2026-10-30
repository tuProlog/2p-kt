@file:Suppress("PrivatePropertyName", "ktlint:standard:property-naming")

package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.TruthImpl
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Test class for [Truth] companion object
 *
 * @author Enrico
 */
class TruthSpec :
    FunSpec({
        val TRUE = TruthImpl(Terms.TRUE_FUNCTOR, true)

        val FAIL = TruthImpl(Terms.FAIL_FUNCTOR, false)

        val FALSE = TruthImpl(Terms.FALSE_FUNCTOR, false)

        test("true-truth-creation") {
            assertSame(Truth.TRUE, Truth.of(true))
        }

        test("true-truth-retrieval") {
            assertEquals(TRUE, Truth.TRUE)
        }

        test("false-truth-creation") {
            assertSame(Truth.FALSE, Truth.of(false))
        }

        test("false-truth-retrieval") {
            assertEquals(FALSE, Truth.FALSE)
        }

        test("fail-truth-retrieval") {
            assertEquals(FAIL, Truth.FAIL)
        }

        withTests(
            nameFn = { "of-string/${it.first}" },
            listOf("true" to Truth.TRUE, "false" to Truth.FALSE, "fail" to Truth.FAIL),
        ) { (string, truth) ->
            assertSame(truth, Truth.of(string))
        }

        test("of-string/rejects-other-atoms") {
            assertFailsWith<IllegalArgumentException> { Truth.of("maybe") }
        }
    })
