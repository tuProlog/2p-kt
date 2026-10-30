@file:Suppress("LocalVariableName")

package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [RealImpl] and [Real]
 *
 * @author Enrico
 */
class RealImplSpec :
    FunSpec({
        val realInstances = RealUtils.bigDecimals.map(::RealImpl)

        test("correct-value") {
            onCorrespondingItems(RealUtils.bigDecimals, realInstances.map { it.value }) { expectedValue, realValue ->
                assertEquals(expectedValue, realValue)
            }
        }

        test("correct-decimal-value") {
            onCorrespondingItems(
                RealUtils.bigDecimals,
                realInstances.map { it.decimalValue },
            ) { expectedValue, realDecimalValue -> assertEquals(expectedValue, realDecimalValue) }
        }

        test("correct-int-value") {
            val expectedIntegers = RealUtils.bigDecimals.map { it.toBigInteger() }

            onCorrespondingItems(expectedIntegers, realInstances.map { it.intValue }) { expectedValue, realIntValue ->
                assertEquals(expectedValue, realIntValue)
            }
        }

        test("correct-to-string") {
            val expectedToString = RealUtils.bigDecimals.map { Real.toStringEnsuringDecimal(it) }

            onCorrespondingItems(
                expectedToString,
                realInstances.map { it.toString() },
            ) { expectedString, realToString ->
                assertEquals(expectedString, realToString)
            }
        }

        test("compare-to-works-as-expected") {
            val oneDotOne = RealImpl(BigDecimal.of(1.1))
            val oneDotTwentyTwo = RealImpl(BigDecimal.of(1.22f))

            assertTrue(oneDotOne.compareValueTo(oneDotOne) == 0)
            assertTrue(oneDotOne.compareValueTo(oneDotOne) >= 0)

            assertTrue(oneDotOne.compareValueTo(oneDotTwentyTwo) != 0)
            assertTrue(oneDotOne.compareValueTo(oneDotTwentyTwo) <= 0)
            assertTrue(oneDotOne.compareValueTo(oneDotTwentyTwo) < 0)

            assertFalse(oneDotOne.compareValueTo(oneDotTwentyTwo) >= 0)
            assertFalse(oneDotOne.compareValueTo(oneDotTwentyTwo) > 0)
        }

        test("is-properties-and-types") {
            realInstances.forEach(TermTypeAssertionUtils::assertIsReal)
        }

        test("equals-works-as-expected") {
            val oneReal = RealImpl(BigDecimal.of(1.1))
            val oneAtom = Atom.of("1.1")

            assertEquals(oneReal, oneReal)

            assertNotEquals<Constant>(oneReal, oneAtom)
        }

        test("structurally-equals-works-as-expected") {
            val oneReal = RealImpl(BigDecimal.of(-2.6))
            val oneAtom = Atom.of("-2.6")

            assertStructurallyEquals(oneReal, oneReal)

            assertNotStructurallyEquals(oneReal, oneAtom)
        }

        test("real-fresh-copy-should-return-the-instance-itself") {
            realInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("real-fresh-copy-with-scope-should-return-the-instance-itself") {
            realInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
        }

        test("apply-always-returns-the-instance-itself") {
            realInstances.forEach(ConstantUtils::assertApplyingSubstitutionIsItself)
        }
    })
