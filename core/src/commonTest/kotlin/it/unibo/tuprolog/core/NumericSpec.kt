package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IntegerUtils
import it.unibo.tuprolog.core.testutils.RealUtils
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertNotEquals

/**
 * Test class for [Numeric] companion object
 *
 * @author Enrico
 */
class NumericSpec :
    FunSpec({
        fun <T> assertEquals(
            expected: T,
            actual: T,
            message: String? = "Failed assertion $expected == $actual",
        ) {
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

        val realToRealWithTrailingZerosMap by lazy {
            RealUtils.stringNumbers
                .filterNot { "E" in it || "e" in it }
                .flatMap {
                    listOf(
                        BigDecimal.of(it) to BigDecimal.of(it + "0"),
                        BigDecimal.of(it) to BigDecimal.of(it + "00"),
                    )
                }
        }

        test("numeric-of-big-decimal") {
            val correct = RealUtils.bigDecimals.map { Real.of(it) }
            val toTest = RealUtils.bigDecimals.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-number") {
            val correctReal = RealUtils.decimalsAsDoubles.map { Real.of(it) }

            @Suppress("USELESS_CAST")
            val toTestReal = RealUtils.decimalsAsDoubles.map { it as Number }.map { Numeric.of(it) }

            val correctInteger = IntegerUtils.bigIntegers.map { Integer.of(it) }

            @Suppress("USELESS_CAST")
            val toTestInteger = IntegerUtils.onlyLongs.map { it as Number }.map { Numeric.of(it) }

            onCorrespondingItems(toTestReal, correctReal, ::assertEqualities)
            onCorrespondingItems(toTestInteger, correctInteger, ::assertEqualities)
        }

        test("numeric-of-double") {
            val correct = RealUtils.decimalsAsDoubles.map { Real.of(it) }
            val toTest = RealUtils.decimalsAsDoubles.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-float") {
            val correct = RealUtils.decimalsAsFloats.map { Real.of(it) }
            val toTest = RealUtils.decimalsAsFloats.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-big-integer") {
            val correct = IntegerUtils.bigIntegers.map { Integer.of(it) }
            val toTest = IntegerUtils.bigIntegers.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-int") {
            val correct = IntegerUtils.onlyInts.map { Integer.of(it) }
            val toTest = IntegerUtils.onlyInts.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-long") {
            val correct = IntegerUtils.onlyLongs.map { Integer.of(it) }
            val toTest = IntegerUtils.onlyLongs.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-short") {
            val correct = IntegerUtils.onlyShorts.map { Integer.of(it) }
            val toTest = IntegerUtils.onlyShorts.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-byte") {
            val correct = IntegerUtils.onlyBytes.map { Integer.of(it) }
            val toTest = IntegerUtils.onlyBytes.map { Numeric.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("numeric-of-string") {
            val correctInteger = IntegerUtils.stringNumbers.map { Integer.of(it) }
            val toTestInteger = IntegerUtils.stringNumbers.map { Numeric.of(it) }

            val correctReal = RealUtils.stringNumbers.map { Real.of(it) }
            val toTestReal = RealUtils.stringNumbers.map { Numeric.of(it) }

            onCorrespondingItems(toTestInteger, correctInteger, ::assertEqualities)
            onCorrespondingItems(toTestReal, correctReal, ::assertEqualities)
        }

        test("integer-not-equal-to-real-even-if-same-value") {
            IntegerUtils.bigIntegers.map { it to it.toDouble() }.forEach { (integer, sameValueToDouble) ->
                assertNotEquals<Numeric>(
                    Numeric.of(integer),
                    Numeric.of(sameValueToDouble),
                )
            }
        }

        test("real-comparison-does-not-take-trailing-zeros-into-account") {
            realToRealWithTrailingZerosMap.forEach { (real, realWithTrailingZeros) ->
                assertEquals(
                    Real.of(real),
                    Real.of(realWithTrailingZeros),
                )
            }
        }

        test("real-hash-code-is-consistent-with-equals") {
            realToRealWithTrailingZerosMap.forEach { (real, realWithTrailingZeros) ->
                assertEquals(
                    Real.of(real).hashCode(),
                    Real.of(realWithTrailingZeros).hashCode(),
                )
            }
        }
    })

private inline val loggingOn get() = false
