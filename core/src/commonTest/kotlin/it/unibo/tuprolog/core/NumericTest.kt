package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IntegralUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import kotlin.test.Test

/**
 * Test class for [Numeric] companion object
 *
 * @author Enrico
 */
internal class NumericTest {

    @Test
    fun numericOfBigDecimal() {
        val correct = RealUtils.bigDecimals.map { Real.of(it) }
        val toTest = RealUtils.bigDecimals.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfNumber() {
        val correctReal = RealUtils.decimalsAsDoubles.map { Real.of(it) }
        @Suppress("USELESS_CAST") val toTestReal =
                RealUtils.decimalsAsDoubles.map { it as Number }.map { Numeric.of(it) }

        val correctIntegral = IntegralUtils.bigIntegers.map { Integral.of(it) }
        @Suppress("USELESS_CAST") val toTestIntegral =
                IntegralUtils.onlyLongs.map { it as Number }.map { Numeric.of(it) }

        onCorrespondingItems(toTestReal, correctReal, ::assertEqualities)
        onCorrespondingItems(toTestIntegral, correctIntegral, ::assertEqualities)
    }

    @Test
    fun numericOfDouble() {
        val correct = RealUtils.decimalsAsDoubles.map { Real.of(it) }
        val toTest = RealUtils.decimalsAsDoubles.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfFloat() {
        val correct = RealUtils.decimalsAsFloats.map { Real.of(it) }
        val toTest = RealUtils.decimalsAsFloats.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfBigInteger() {
        val correct = IntegralUtils.bigIntegers.map { Integral.of(it) }
        val toTest = IntegralUtils.bigIntegers.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfInt() {
        val correct = IntegralUtils.onlyInts.map { Integral.of(it) }
        val toTest = IntegralUtils.onlyInts.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfLong() {
        val correct = IntegralUtils.onlyLongs.map { Integral.of(it) }
        val toTest = IntegralUtils.onlyLongs.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfShort() {
        val correct = IntegralUtils.onlyShorts.map { Integral.of(it) }
        val toTest = IntegralUtils.onlyShorts.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfByte() {
        val correct = IntegralUtils.onlyBytes.map { Integral.of(it) }
        val toTest = IntegralUtils.onlyBytes.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfString() {
        val correctIntegral = IntegralUtils.stringNumbers.map { Integral.of(it) }
        val toTestIntegral = IntegralUtils.stringNumbers.map { Numeric.of(it) }

        val correctReal = RealUtils.stringNumbers.map { Real.of(it) }
        val toTestReal = RealUtils.stringNumbers.map { Numeric.of(it) }

        onCorrespondingItems(toTestIntegral, correctIntegral, ::assertEqualities)
        onCorrespondingItems(toTestReal, correctReal, ::assertEqualities)
    }
}
