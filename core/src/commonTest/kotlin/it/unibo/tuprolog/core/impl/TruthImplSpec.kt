package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [TruthImpl] and [Truth]
 *
 * @author Enrico
 */
class TruthImplSpec :
    FunSpec({
        val truthInstances = listOf(Truth.TRUE, Truth.FAIL, Truth.FALSE)

        test("truth-functor") {
            assertEquals("true", Truth.TRUE.functor)
            assertEquals("fail", Truth.FAIL.functor)
        }

        test("is-properties-and-types-for-true") {
            TermTypeAssertionUtils.assertIsTruth(Truth.TRUE)
            assertTrue(Truth.TRUE.isTrue)
        }

        test("is-properties-and-types-for-fail") {
            TermTypeAssertionUtils.assertIsTruth(Truth.FAIL)
            assertTrue(Truth.FAIL.isFail)
        }

        test("truth-fresh-copy-should-return-the-instance-itself") {
            truthInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("truth-fresh-copy-with-scope-should-return-the-instance-itself") {
            truthInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
        }
    })
