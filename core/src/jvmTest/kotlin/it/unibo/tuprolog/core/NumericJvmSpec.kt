package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class NumericJvmSpec :
    FunSpec({
        test("of-number/other-numbers-go-through-their-string") {
            assertEquals(Integer.of("12345678901234567890"), Numeric.of(java.math.BigInteger("12345678901234567890")))
        }
    })
