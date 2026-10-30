@file:Suppress("LocalVariableName")

package it.unibo.tuprolog.core.impl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [IntegerImpl] and [Integer]
 *
 * @author Enrico
 */
class IntegerImplSpec :
    FunSpec({
        val integerInstances = IntegerUtils.bigIntegers.map(::IntegerImpl)

        test("correct-value") {
            onCorrespondingItems(
                IntegerUtils.bigIntegers,
                integerInstances.map { it.value },
            ) { expectedValue, integerValue -> assertEquals(expectedValue, integerValue) }
        }

        test("correct-int-value") {
            onCorrespondingItems(
                IntegerUtils.bigIntegers,
                integerInstances.map { it.intValue },
            ) { expectedValue, integerIntValue -> assertEquals(expectedValue, integerIntValue) }
        }

        test("correct-decimal-value") {
            val expectedDecimals = IntegerUtils.bigIntegers.map { BigDecimal.of(it) }

            onCorrespondingItems(
                expectedDecimals,
                integerInstances.map { it.decimalValue },
            ) { expectedValue, integerDecimalValue -> assertEquals(expectedValue, integerDecimalValue) }
        }

        test("correct-to-string") {
            val expectedToString = IntegerUtils.bigIntegers.map { it.toString() }

            onCorrespondingItems(
                expectedToString,
                integerInstances.map { it.toString() },
            ) { expectedString, integerToString -> assertEquals(expectedString, integerToString) }
        }

        test("compare-to-works-as-expected") {
            val one = IntegerImpl(BigInteger.of(1))
            val two = IntegerImpl(BigInteger.of(2))

            assertTrue(one.compareValueTo(one) == 0)
            assertTrue(one.compareValueTo(one) >= 0)

            assertTrue(one.compareValueTo(two) != 0)
            assertTrue(one.compareValueTo(two) <= 0)
            assertTrue(one.compareValueTo(two) < 0)

            assertFalse(one.compareValueTo(two) >= 0)
            assertFalse(one.compareValueTo(two) > 0)
        }

        test("is-properties-and-types") {
            integerInstances.forEach(TermTypeAssertionUtils::assertIsInteger)
        }

        test("equals-works-as-expected") {
            val oneInteger = IntegerImpl(BigInteger.of(1))
            val oneReal = Real.of(1.0)
            val oneAtom = Atom.of("1")

            assertEquals(oneInteger, oneInteger)

            assertNotEquals<Numeric>(oneInteger, oneReal)
            assertNotEquals<Constant>(oneInteger, oneAtom)
        }

        test("structurally-equals-works-as-expected") {
            val oneInteger = IntegerImpl(BigInteger.of(1))
            val oneReal = Real.of(1.0)
            val oneAtom = Atom.of("1")

            assertStructurallyEquals(oneInteger, oneInteger)
            assertStructurallyEquals(oneInteger, oneReal)

            assertNotStructurallyEquals(oneInteger, oneAtom)
        }

        test("integer-fresh-copy-should-return-the-instance-itself") {
            integerInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
        }

        test("integer-fresh-copy-with-scope-should-return-the-instance-itself") {
            integerInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
        }

        test("apply-always-returns-the-instance-itself") {
            integerInstances.forEach(ConstantUtils::assertApplyingSubstitutionIsItself)
        }
    })
