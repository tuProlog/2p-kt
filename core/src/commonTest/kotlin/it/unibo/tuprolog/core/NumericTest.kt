package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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

        val correctInteger = IntegerUtils.bigIntegers.map { Integer.of(it) }
        @Suppress("USELESS_CAST") val toTestInteger =
                IntegerUtils.onlyLongs.map { it as Number }.map { Numeric.of(it) }

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
    fun test1() {
        // TODO @enrico, please sibonify this test
        assertNotEquals<Numeric>(
                Numeric.of(1),
                Numeric.of(1.0),
                "Integers are different from reals"
        )
    }

    @Test
    fun test2() {
        // TODO @enrico, please sibonify this test
        assertEquals(
                Real.of(BigDecimal.of(100, 1)),
                Real.of(BigDecimal.of(1000, 2)),
                "Comparison among reals does not takes trailing zeros into account"
        )
        assertEquals(
                Real.of(BigDecimal.of("10.0")),
                Real.of(BigDecimal.of("10.00")),
                "Comparison among reals does not takes trailing zeros into account"
        )
    }

    @Test
    fun test3() {
        // TODO @enrico, please sibonify this test
        assertEquals(
                Real.of(BigDecimal.of(100, 1)).hashCode(),
                Real.of(BigDecimal.of(1000, 2)).hashCode(),
                "Hash code of reals is consistent with equals"
        )
    }
}
