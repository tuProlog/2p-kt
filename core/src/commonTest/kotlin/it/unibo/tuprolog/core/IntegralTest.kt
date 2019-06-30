package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegralImpl
import it.unibo.tuprolog.core.testutils.EqualityUtils
import it.unibo.tuprolog.core.testutils.IntegralUtils
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test

/**
 * Test class for [Integral] companion object
 *
 * @author Enrico
 */
internal class IntegralTest {

    @Test
    fun integralOfBigInteger() {
        val correct = IntegralUtils.bigIntegers.map(::IntegralImpl)
        val toTest = IntegralUtils.bigIntegers.map { Integral.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralOfLong() {
        val correct = IntegralUtils.onlyLongs.map(::IntegralImpl)
        val toTest = IntegralUtils.onlyLongs.map { Integral.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralOfInt() {
        val correct = IntegralUtils.onlyInts.map(::IntegralImpl)
        val toTest = IntegralUtils.onlyInts.map { Integral.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralOfShort() {
        val correct = IntegralUtils.onlyShorts.map(::IntegralImpl)
        val toTest = IntegralUtils.onlyShorts.map { Integral.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralOfByte() {
        val correct = IntegralUtils.onlyBytes.map(::IntegralImpl)
        val toTest = IntegralUtils.onlyBytes.map { Integral.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralOfString() {
        val correct = IntegralUtils.bigIntegers.map(::IntegralImpl)
        val toTest = IntegralUtils.stringNumbers.map { Integral.of(it) }

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralOfStringWithRadix() {
        val correct = IntegralImpl(BigInteger.of(34))
        val toTest = Integral.of("22", 16)

        EqualityUtils.assertEqualities(toTest, correct)
    }

    @Test
    fun integralsRespectItsRegexPattern() {
        IntegralUtils.stringNumbers.forEach { it matches Integral.INTEGRAL_REGEX_PATTERN }
        IntegralUtils.stringNumbers.map { Integral.of(it) }.forEach { it.toString() matches Integral.INTEGRAL_REGEX_PATTERN }
    }
}
