package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.testutils.ConstantUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        RealUtils.bigDecimals.zip(realInstances).forEach { (expectedValue, real) ->
            assertEquals(expectedValue, real.value)
        }
    }

    @Test
    fun correctDecimalValue() {
        RealUtils.bigDecimals.zip(realInstances).forEach { (expectedValue, real) ->
            assertEquals(expectedValue, real.decimalValue)
        }
    }

    @Test
    fun correctIntValue() {
        val expectedIntegrals = RealUtils.bigDecimals.map { it.toBigInteger() }

        expectedIntegrals.zip(realInstances).forEach { (expectedValue, real) ->
            assertEquals(expectedValue, real.intValue)
        }
    }

    @Test
    fun correctToString() {
        val expectedToString = RealUtils.bigDecimals.map { it.toString() }

        expectedToString.zip(realInstances).forEach { (expectedString, integral) ->
            assertEquals(expectedString, integral.toString())
        }
    }

    @Suppress("LocalVariableName")
    @Test
    fun compareToWorksAsExpected() {
        val `1,1` = RealImpl(BigDecimal.of(1.1))
        val `1,22f` = RealImpl(BigDecimal.of(1.22f))

        assertTrue(`1,1`.compareTo(`1,1`) == 0)
        assertTrue(`1,1` >= `1,1`)

        assertTrue(`1,1`.compareTo(`1,22f`) != 0)
        assertTrue(`1,1` <= `1,22f`)
        assertTrue(`1,1` < `1,22f`)

        assertFalse(`1,1` >= `1,22f`)
        assertFalse(`1,1` > `1,22f`)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        realInstances.forEach(TermTypeAssertionUtils::assertIsReal)
    }

    @Test
    fun strictlyEqualsWorksAsExpected() {
        val oneIntegral = RealImpl(BigDecimal.of(1.1))
        val oneAtom = Atom.of("1.1")

        assertTrue(oneIntegral strictlyEquals oneIntegral)

        assertFalse(oneIntegral strictlyEquals oneAtom)
        assertFalse(oneAtom strictlyEquals oneIntegral)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val oneIntegral = RealImpl(BigDecimal.of(-2.6))
        val oneAtom = Atom.of("-2.6")

        assertTrue(oneIntegral structurallyEquals oneIntegral)

        assertFalse(oneIntegral structurallyEquals oneAtom)
        assertFalse(oneAtom structurallyEquals oneIntegral)
    }

    @Test
    fun integralFreshCopyShouldReturnTheInstanceItself() {
        realInstances.forEach(ConstantUtils::assertFreshCopyIsItself)
    }
}
