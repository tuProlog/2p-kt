package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.FactUtils
import it.unibo.tuprolog.core.testutils.StructUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [FactImpl] and [Fact]
 *
 * @author Enrico
 */
class FactImplSpec :
    FunSpec({
        val mixedFactInstances by lazy { FactUtils.mixedFacts.map(::FactImpl) }

        val groundFactInstances by lazy { FactUtils.groundFacts.map(::FactImpl) }

        val nonGroundFactInstances by lazy { FactUtils.nonGroundFacts.map(::FactImpl) }

        test("head-correct") {
            onCorrespondingItems(FactUtils.mixedFacts, mixedFactInstances.map { it.head }, ::assertEqualities)
        }

        test("args-correct") {
            val correctFactArgs = FactUtils.mixedFacts.map { listOf(it, Truth.TRUE) }

            onCorrespondingItems(correctFactArgs, mixedFactInstances.map { it.args }) { expected, actual ->
                assertEquals(expected, actual)
            }
        }

        test("arity-is-two") {
            mixedFactInstances.forEach { assertEquals(2, it.arity) }
        }

        test("body-is-true") {
            mixedFactInstances.forEach { assertSame(Truth.TRUE, it.body) }
        }

        test("is-well-formed-always-true") {
            mixedFactInstances.forEach { assertTrue { it.isWellFormed } }
        }

        test("is-properties-and-types") {
            mixedFactInstances.forEach(TermTypeAssertionUtils::assertIsFact)
        }

        test("is-ground-true-if-no-variables-are-present") {
            groundFactInstances.forEach { assertTrue { it.isGround } }
            nonGroundFactInstances.forEach { assertFalse { it.isGround } }
        }

        test("fresh-copy-should-return-instance-itself-if-ground") {
            groundFactInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("fresh-copy-should-renew-variables") {
            nonGroundFactInstances.forEach(StructUtils::assertFreshCopyRenewsContainedVariables)
        }
    })
