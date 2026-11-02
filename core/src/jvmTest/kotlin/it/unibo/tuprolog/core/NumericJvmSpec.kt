package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NumericJvmSpec :
    FunSpec({
        test("of-number/other-numbers-go-through-their-string") {
            Numeric.of(java.math.BigInteger("12345678901234567890")) shouldBe Integer.of("12345678901234567890")
        }
    })
