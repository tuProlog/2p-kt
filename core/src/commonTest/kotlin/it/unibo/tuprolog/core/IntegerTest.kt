package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegerImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IntegerUtils
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test

/**
 * Test class for [Integer] companion object
 *
 * @author Enrico
 */
internal class IntegerTest {
    @Test
    fun integerOfBigInteger() {
        val correct = IntegerUtils.bigIntegers.map(::IntegerImpl)
        val toTest = IntegerUtils.bigIntegers.map { Integer.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun integerOfLong() {
        val correct = IntegerUtils.onlyLongs.map { BigInteger.of(it) }.map(::IntegerImpl)
        val toTest = IntegerUtils.onlyLongs.map { Integer.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun integerOfInt() {
        val correct = IntegerUtils.onlyInts.map { BigInteger.of(it) }.map(::IntegerImpl)
        val toTest = IntegerUtils.onlyInts.map { Integer.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun integerOfShort() {
        val correct = IntegerUtils.onlyShorts.map { BigInteger.of(it.toLong()) }.map(::IntegerImpl)
        val toTest = IntegerUtils.onlyShorts.map { Integer.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun integerOfByte() {
        val correct = IntegerUtils.onlyBytes.map { BigInteger.of(it.toLong()) }.map(::IntegerImpl)
        val toTest = IntegerUtils.onlyBytes.map { Integer.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun integerOfString() {
        val correct = IntegerUtils.bigIntegers.map(::IntegerImpl)
        val toTest = IntegerUtils.stringNumbers.map { Integer.of(it) }

        onCorrespondingItems(toTest, correct, ::assertEqualities)
    }

    @Test
    fun integerOfStringWithRadix() {
        val correct = IntegerImpl(BigInteger.of(34))
        val toTest = Integer.of("22", 16)

        assertEqualities(toTest, correct)
    }

    @Test
    fun integersRespectItsRegexPattern() {
        IntegerUtils.stringNumbers.forEach { it matches Integer.PATTERN }
        IntegerUtils.stringNumbers
            .map { Integer.of(it) }
            .forEach { it.toString() matches Integer.PATTERN }
    }
}
