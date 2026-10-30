package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import org.gciatto.kt.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumberTypeTesterSpec :
    FunSpec({
        val tester = NumberTypeTester()

        test("positive-integer-is-recognized-as-integer") {
            assertTrue(tester.numberIsInteger(5))
            assertEquals(BigInteger.of("5"), tester.numberToInteger(5))
        }

        test("zero-is-recognized-as-integer") {
            assertTrue(tester.numberIsInteger(0))
            assertEquals(BigInteger.of("0"), tester.numberToInteger(0))
        }

        test("negative-integer-is-recognized-as-integer") {
            // Regression test: the "-?" was missing from the detection regex, so a negative number
            // (e.g. -1) was misclassified as a decimal instead of an integer.
            assertTrue(tester.numberIsInteger(-1))
            assertEquals(BigInteger.of("-1"), tester.numberToInteger(-1))
        }

        test("decimal-is-not-recognized-as-integer") {
            assertFalse(tester.numberIsInteger(5.5))
        }

        test("negative-decimal-is-not-recognized-as-integer") {
            assertFalse(tester.numberIsInteger(-5.5))
        }
    })
