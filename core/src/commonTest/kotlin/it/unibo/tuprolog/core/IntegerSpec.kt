package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.IntegerImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.IntegerUtils
import org.gciatto.kt.math.BigInteger

/**
 * Test class for [Integer] companion object
 *
 * @author Enrico
 */
class IntegerSpec :
    FunSpec({
        test("integer-of-big-integer") {
            val correct = IntegerUtils.bigIntegers.map(::IntegerImpl)
            val toTest = IntegerUtils.bigIntegers.map { Integer.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("integer-of-long") {
            val correct = IntegerUtils.onlyLongs.map { BigInteger.of(it) }.map(::IntegerImpl)
            val toTest = IntegerUtils.onlyLongs.map { Integer.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("integer-of-int") {
            val correct = IntegerUtils.onlyInts.map { BigInteger.of(it) }.map(::IntegerImpl)
            val toTest = IntegerUtils.onlyInts.map { Integer.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("integer-of-short") {
            val correct = IntegerUtils.onlyShorts.map { BigInteger.of(it.toLong()) }.map(::IntegerImpl)
            val toTest = IntegerUtils.onlyShorts.map { Integer.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("integer-of-byte") {
            val correct = IntegerUtils.onlyBytes.map { BigInteger.of(it.toLong()) }.map(::IntegerImpl)
            val toTest = IntegerUtils.onlyBytes.map { Integer.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("integer-of-string") {
            val correct = IntegerUtils.bigIntegers.map(::IntegerImpl)
            val toTest = IntegerUtils.stringNumbers.map { Integer.of(it) }

            onCorrespondingItems(toTest, correct, ::assertEqualities)
        }

        test("integer-of-string-with-radix") {
            val correct = IntegerImpl(BigInteger.of(34))
            val toTest = Integer.of("22", 16)

            assertEqualities(toTest, correct)
        }

        test("integers-respect-its-regex-pattern") {
            IntegerUtils.stringNumbers.forEach { it matches Integer.PATTERN }
            IntegerUtils.stringNumbers
                .map { Integer.of(it) }
                .forEach { it.toString() matches Integer.PATTERN }
        }
    })
