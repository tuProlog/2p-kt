package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils

/**
 * Test class for [TruthImpl] and [Truth]
 *
 * @author Enrico
 */
class TruthImplSpec :
    FunSpec({
        val truthInstances = listOf(Truth.TRUE, Truth.FAIL, Truth.FALSE)

        test("truth-functor") {
            Truth.TRUE.functor shouldBe "true"
            Truth.FAIL.functor shouldBe "fail"
        }

        test("is-properties-and-types-for-true") {
            TermTypeAssertionUtils.assertIsTruth(Truth.TRUE)
            Truth.TRUE.isTrue.shouldBeTrue()
        }

        test("is-properties-and-types-for-fail") {
            TermTypeAssertionUtils.assertIsTruth(Truth.FAIL)
            Truth.FAIL.isFail.shouldBeTrue()
        }

        withTests(nameFn = { "fresh-copy-is-itself/${it.value}" }, truthInstances) { truth ->
            ConstantUtils.assertFreshCopyIsItself(truth)
        }

        withTests(nameFn = { "fresh-copy-with-scope-is-itself/${it.value}" }, truthInstances) { truth ->
            ConstantUtils.assertFreshCopyWithScopeIsItself(truth)
        }
    })
