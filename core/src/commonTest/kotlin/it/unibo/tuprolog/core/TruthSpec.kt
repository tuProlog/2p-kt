@file:Suppress("PrivatePropertyName", "ktlint:standard:property-naming")

package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.impl.TruthImpl

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
            Truth.of(true) shouldBeSameInstanceAs Truth.TRUE
        }

        test("true-truth-retrieval") {
            Truth.TRUE shouldBe TRUE
        }

        test("false-truth-creation") {
            Truth.of(false) shouldBeSameInstanceAs Truth.FALSE
        }

        test("false-truth-retrieval") {
            Truth.FALSE shouldBe FALSE
        }

        test("fail-truth-retrieval") {
            Truth.FAIL shouldBe FAIL
        }

        withTests(
            nameFn = { "of-string/${it.first}" },
            listOf("true" to Truth.TRUE, "false" to Truth.FALSE, "fail" to Truth.FAIL),
        ) { (string, truth) ->
            Truth.of(string) shouldBeSameInstanceAs truth
        }

        test("of-string/rejects-other-atoms") {
            shouldThrow<IllegalArgumentException> { Truth.of("maybe") }
        }
    })
