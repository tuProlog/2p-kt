package it.unibo.tuprolog.core.impl

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [IntegerImpl] and [Integer]
 *
 * @author Enrico
 */
class IntegerImplTest {
    private val integerInstances = IntegerUtils.bigIntegers.map(::IntegerImpl)

    @Test
    fun correctValue() {
        onCorrespondingItems(
            IntegerUtils.bigIntegers,
            integerInstances.map { it.value },
        ) { expectedValue, integerValue -> assertEquals(expectedValue, integerValue) }
    }

    @Test
    fun correctIntValue() {
        onCorrespondingItems(
            IntegerUtils.bigIntegers,
            integerInstances.map { it.intValue },
        ) { expectedValue, integerIntValue -> assertEquals(expectedValue, integerIntValue) }
    }

    @Test
    fun correctDecimalValue() {
        val expectedDecimals = IntegerUtils.bigIntegers.map { BigDecimal.of(it) }

        onCorrespondingItems(
            expectedDecimals,
            integerInstances.map { it.decimalValue },
        ) { expectedValue, integerDecimalValue -> assertEquals(expectedValue, integerDecimalValue) }
    }

    @Test
    fun correctToString() {
        val expectedToString = IntegerUtils.bigIntegers.map { it.toString() }

        onCorrespondingItems(
            expectedToString,
            integerInstances.map { it.toString() },
        ) { expectedString, integerToString -> assertEquals(expectedString, integerToString) }
    }

    @Suppress("LocalVariableName")
    @Test
    fun compareToWorksAsExpected() {
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

    @Test
    fun testIsPropertiesAndTypes() {
        integerInstances.forEach(TermTypeAssertionUtils::assertIsInteger)
    }

    @Test
    fun equalsWorksAsExpected() {
        val oneInteger = IntegerImpl(BigInteger.of(1))
        val oneReal = Real.of(1.0)
        val oneAtom = Atom.of("1")

        assertEquals(oneInteger, oneInteger)

        assertNotEquals<Numeric>(oneInteger, oneReal)
        assertNotEquals<Constant>(oneInteger, oneAtom)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val oneInteger = IntegerImpl(BigInteger.of(1))
        val oneReal = Real.of(1.0)
        val oneAtom = Atom.of("1")

        assertStructurallyEquals(oneInteger, oneInteger)
        assertStructurallyEquals(oneInteger, oneReal)

        assertNotStructurallyEquals(oneInteger, oneAtom)
    }

    @Test
    fun integerFreshCopyShouldReturnTheInstanceItself() {
        integerInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun integerFreshCopyWithScopeShouldReturnTheInstanceItself() {
        integerInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }

    @Test
    fun applyAlwaysReturnsTheInstanceItself() {
        integerInstances.forEach(ConstantUtils::assertApplyingSubstitutionIsItself)
    }
}
