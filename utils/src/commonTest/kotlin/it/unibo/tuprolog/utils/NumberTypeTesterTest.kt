package it.unibo.tuprolog.utils

import org.gciatto.kt.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumberTypeTesterTest {
    private val tester = NumberTypeTester()

    @Test
    fun testPositiveIntegerIsRecognizedAsInteger() {
        assertTrue(tester.numberIsInteger(5))
        assertEquals(BigInteger.of("5"), tester.numberToInteger(5))
    }

    @Test
    fun testZeroIsRecognizedAsInteger() {
        assertTrue(tester.numberIsInteger(0))
        assertEquals(BigInteger.of("0"), tester.numberToInteger(0))
    }

    @Test
    fun testNegativeIntegerIsRecognizedAsInteger() {
        // Regression test: the "-?" was missing from the detection regex, so a negative number
        // (e.g. -1) was misclassified as a decimal instead of an integer.
        assertTrue(tester.numberIsInteger(-1))
        assertEquals(BigInteger.of("-1"), tester.numberToInteger(-1))
    }

    @Test
    fun testDecimalIsNotRecognizedAsInteger() {
        assertFalse(tester.numberIsInteger(5.5))
    }

    @Test
    fun testNegativeDecimalIsNotRecognizedAsInteger() {
        assertFalse(tester.numberIsInteger(-5.5))
    }
}
