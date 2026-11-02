package it.unibo.tuprolog.utils

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.gciatto.kt.math.BigInteger

class NumberTypeTesterSpec :
    FunSpec({
        val tester = NumberTypeTester()

        test("positive-integer-is-recognized-as-integer") {
            tester.numberIsInteger(5).shouldBeTrue()
            tester.numberToInteger(5) shouldBe BigInteger.of("5")
        }

        test("zero-is-recognized-as-integer") {
            tester.numberIsInteger(0).shouldBeTrue()
            tester.numberToInteger(0) shouldBe BigInteger.of("0")
        }

        test("negative-integer-is-recognized-as-integer") {
            // Regression test: the "-?" was missing from the detection regex, so a negative number
            // (e.g. -1) was misclassified as a decimal instead of an integer.
            tester.numberIsInteger(-1).shouldBeTrue()
            tester.numberToInteger(-1) shouldBe BigInteger.of("-1")
        }

        test("decimal-is-not-recognized-as-integer") {
            tester.numberIsInteger(5.5).shouldBeFalse()
        }

        test("negative-decimal-is-not-recognized-as-integer") {
            tester.numberIsInteger(-5.5).shouldBeFalse()
        }
    })
