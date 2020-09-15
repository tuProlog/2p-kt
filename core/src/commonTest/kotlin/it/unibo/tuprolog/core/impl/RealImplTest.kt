package it.unibo.tuprolog.core.impl

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [RealImpl] and [Real]
 *
 * @author Enrico
 */
internal class RealImplTest {

    private val realInstances = RealUtils.bigDecimals.map(::RealImpl)

    @Test
    fun correctValue() {
        onCorrespondingItems(RealUtils.bigDecimals, realInstances.map { it.value }) { expectedValue, realValue ->
            assertEquals(expectedValue, realValue)
        }
    }

    @Test
    fun correctDecimalValue() {
        onCorrespondingItems(
            RealUtils.bigDecimals,
            realInstances.map { it.decimalValue }
        ) { expectedValue, realDecimalValue -> assertEquals(expectedValue, realDecimalValue) }
    }

    @Test
    fun correctIntValue() {
        val expectedIntegers = RealUtils.bigDecimals.map { it.toBigInteger() }

        onCorrespondingItems(expectedIntegers, realInstances.map { it.intValue }) { expectedValue, realIntValue ->
            assertEquals(expectedValue, realIntValue)
        }
    }

    @Test
    fun correctToString() {
        val expectedToString = RealUtils.bigDecimals.map { Real.toStringEnsuringDecimal(it) }

        onCorrespondingItems(expectedToString, realInstances.map { it.toString() }) { expectedString, realToString ->
            assertEquals(expectedString, realToString)
        }
    }

    @Suppress("LocalVariableName")
    @Test
    fun compareToWorksAsExpected() {
        val `1,1` = RealImpl(BigDecimal.of(1.1))
        val `1,22f` = RealImpl(BigDecimal.of(1.22f))

        assertTrue(`1,1`.compareValueTo(`1,1`) == 0)
        assertTrue(`1,1`.compareValueTo(`1,1`) >= 0)

        assertTrue(`1,1`.compareValueTo(`1,22f`) != 0)
        assertTrue(`1,1`.compareValueTo(`1,22f`) <= 0)
        assertTrue(`1,1`.compareValueTo(`1,22f`) < 0)

        assertFalse(`1,1`.compareValueTo(`1,22f`) >= 0)
        assertFalse(`1,1`.compareValueTo(`1,22f`) > 0)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        realInstances.forEach(TermTypeAssertionUtils::assertIsReal)
    }

    @Test
    fun equalsWorksAsExpected() {
        val oneReal = RealImpl(BigDecimal.of(1.1))
        val oneAtom = Atom.of("1.1")

        assertEquals(oneReal, oneReal)

        assertNotEquals<Constant>(oneReal, oneAtom)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val oneReal = RealImpl(BigDecimal.of(-2.6))
        val oneAtom = Atom.of("-2.6")

        assertStructurallyEquals(oneReal, oneReal)

        assertNotStructurallyEquals(oneReal, oneAtom)
    }

    @Test
    fun realFreshCopyShouldReturnTheInstanceItself() {
        realInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }

    @Test
    fun realFreshCopyWithScopeShouldReturnTheInstanceItself() {
        realInstances.forEach(ConstantUtils::assertFreshCopyWithScopeIsItself)
    }

    @Test
    fun applyAlwaysReturnsTheInstanceItself() {
        realInstances.forEach(ConstantUtils::assertApplyingSubstitutionIsItself)
    }
}
