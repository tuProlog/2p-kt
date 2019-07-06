package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integral
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.IntegralUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [IntegralImpl] and [Integral]
 *
 * @author Enrico
 */
class IntegralImplTest {

    private val integralInstances = IntegralUtils.bigIntegers.map(::IntegralImpl)

    @Test
    fun correctValue() {
        onCorrespondingItems(IntegralUtils.bigIntegers, integralInstances.map { it.value }) { expectedValue, integralValue ->
            assertEquals(expectedValue, integralValue)
        }
    }

    @Test
    fun correctIntValue() {
        onCorrespondingItems(IntegralUtils.bigIntegers, integralInstances.map { it.intValue }) { expectedValue, integralIntValue ->
            assertEquals(expectedValue, integralIntValue)
        }
    }

    @Test
    fun correctDecimalValue() {
        val expectedDecimals = IntegralUtils.bigIntegers.map { BigDecimal.of(it) }

        onCorrespondingItems(expectedDecimals, integralInstances.map { it.decimalValue }) { expectedValue, integralDecimalValue ->
            assertEquals(expectedValue, integralDecimalValue)
        }
    }

    @Test
    fun correctToString() {
        val expectedToString = IntegralUtils.bigIntegers.map { it.toString() }

        onCorrespondingItems(expectedToString, integralInstances.map { it.toString() }) { expectedString, integralToString ->
            assertEquals(expectedString, integralToString)
        }
    }

    @Suppress("LocalVariableName")
    @Test
    fun compareToWorksAsExpected() {
        val `1` = IntegralImpl(BigInteger.of(1))
        val `2` = IntegralImpl(BigInteger.of(2))

        assertTrue(`1`.compareTo(`1`) == 0)
        assertTrue(`1` >= `1`)

        assertTrue(`1`.compareTo(`2`) != 0)
        assertTrue(`1` <= `2`)
        assertTrue(`1` < `2`)

        assertFalse(`1` >= `2`)
        assertFalse(`1` > `2`)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        integralInstances.forEach(TermTypeAssertionUtils::assertIsIntegral)
    }

    @Test
    fun strictlyEqualsWorksAsExpected() {
        val oneIntegral = IntegralImpl(BigInteger.of(1))
        val oneReal = Real.of(1.0)
        val oneAtom = Atom.of("1")

        assertStrictlyEquals(oneIntegral, oneIntegral)

        assertNotStrictlyEquals(oneIntegral, oneReal)
        assertNotStrictlyEquals(oneIntegral, oneAtom)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val oneIntegral = IntegralImpl(BigInteger.of(1))
        val oneReal = Real.of(1.0)
        val oneAtom = Atom.of("1")

        assertStructurallyEquals(oneIntegral, oneIntegral)
        assertStructurallyEquals(oneIntegral, oneReal)

        assertNotStructurallyEquals(oneIntegral, oneAtom)
    }

    @Test
    fun integralFreshCopyShouldReturnTheInstanceItself() {
        integralInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun integralFreshCopyWithScopeShouldReturnTheInstanceItself() {
        integralInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }
}
