package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertNotEquals

/**
 * Test class for [Numeric] companion object
 *
 * @author Enrico
 */
internal class NumericTest {

    private inline val loggingOn get() = false

    fun <T> assertEquals(expected: T, actual: T, message: String? = "Failed assertion $expected == $actual") {
        if (loggingOn) println("Object\n\t$actual\nis expected to be equal to\n\t$expected")
        try {
            kotlin.test.assertEquals(expected, actual, message)
        } catch (e: Exception) {
            if (loggingOn) println(e.message)
            throw e
        } finally {
            if (loggingOn) println("".padEnd(80, '-'))
        }
    }

    /** This map contains a mapping between real numbers and their version with some trailing zeros (i.e. 10.0 to 10.00) */
    private val realToRealWithTrailingZerosMap by lazy {
        RealUtils.decimalsAsFloats
            .map { BigDecimal.of(it) }
            .map {
                BigDecimal.of(it.unscaledValue.toLong(), it.scale) to
                        BigDecimal.of(it.unscaledValue.toLong() * 10, it.scale + 1)
            } +
                RealUtils.stringNumbers
                    .filterNot { "E" in it || "e" in it }
                    .map { BigDecimal.of(it) to BigDecimal.of(it + "00") }
    }

    @Test
    fun numericOfBigDecimal() {
        val correct = RealUtils.bigDecimals.map { Real.of(it) }
        val toTest = RealUtils.bigDecimals.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfNumber() {
        val correctReal = RealUtils.decimalsAsDoubles.map { Real.of(it) }
        @Suppress("USELESS_CAST")
        val toTestReal = RealUtils.decimalsAsDoubles.map { it as Number }.map { Numeric.of(it) }

        val correctInteger = IntegerUtils.bigIntegers.map { Integer.of(it) }
        @Suppress("USELESS_CAST")
        val toTestInteger = IntegerUtils.onlyLongs.map { it as Number }.map { Numeric.of(it) }

        onCorrespondingItems(toTestReal, correctReal, ::assertEqualities)
        onCorrespondingItems(toTestInteger, correctInteger, ::assertEqualities)
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
        val correct = IntegerUtils.bigIntegers.map { Integer.of(it) }
        val toTest = IntegerUtils.bigIntegers.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfInt() {
        val correct = IntegerUtils.onlyInts.map { Integer.of(it) }
        val toTest = IntegerUtils.onlyInts.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfLong() {
        val correct = IntegerUtils.onlyLongs.map { Integer.of(it) }
        val toTest = IntegerUtils.onlyLongs.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfShort() {
        val correct = IntegerUtils.onlyShorts.map { Integer.of(it) }
        val toTest = IntegerUtils.onlyShorts.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfByte() {
        val correct = IntegerUtils.onlyBytes.map { Integer.of(it) }
        val toTest = IntegerUtils.onlyBytes.map { Numeric.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun numericOfString() {
        val correctInteger = IntegerUtils.stringNumbers.map { Integer.of(it) }
        val toTestInteger = IntegerUtils.stringNumbers.map { Numeric.of(it) }

        val correctReal = RealUtils.stringNumbers.map { Real.of(it) }
        val toTestReal = RealUtils.stringNumbers.map { Numeric.of(it) }

        onCorrespondingItems(toTestInteger, correctInteger, ::assertEqualities)
        onCorrespondingItems(toTestReal, correctReal, ::assertEqualities)
    }

    @Test
    fun integerNotEqualToRealEvenIfSameValue() {
        IntegerUtils.bigIntegers.map { it to it.toDouble() }.forEach { (integer, sameValueToDouble) ->
            assertNotEquals<Numeric>(
                Numeric.of(integer),
                Numeric.of(sameValueToDouble)
            )
        }
    }

    @Test
    fun realComparisonDoesNotTakeTrailingZerosIntoAccount() {
        realToRealWithTrailingZerosMap.forEach { (real, realWithTrailingZeros) ->
            assertEquals(
                Real.of(real),
                Real.of(realWithTrailingZeros)
            )
        }
    }

    @Test
    fun realHashCodeIsConsistentWithEquals() {
        realToRealWithTrailingZerosMap.forEach { (real, realWithTrailingZeros) ->
            assertEquals(
                Real.of(real).hashCode(),
                Real.of(realWithTrailingZeros).hashCode()
            )
        }
    }
}
