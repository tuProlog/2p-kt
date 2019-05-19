package it.unibo.tuprolog.core

import io.github.gciatto.kt.math.BigInteger
import kotlin.collections.List

class TestIntegrals : BaseTestNumeric() {

    override val numbersUnderTestAsStrings: List<String> = listOf(
            "0",
            "1",
            "2",
            "-1",
            "-2",
            Int.MAX_VALUE.toString(),
            Int.MIN_VALUE.toString(),
            Long.MAX_VALUE.toString(),
            Long.MIN_VALUE.toString(),
            "0xf",
            "0XA",
            "0b0",
            "0B1",
            "0o7",
            "0O10"
    )

    val numbersUnderTestAsBigIntegers: List<BigInteger> = listOf(
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.TWO,
            -BigInteger.ONE,
            -BigInteger.TWO,
            BigInteger.of(Int.MAX_VALUE),
            BigInteger.of(Int.MIN_VALUE),
            BigInteger.of(Long.MAX_VALUE),
            BigInteger.of(Long.MIN_VALUE),
            BigInteger.of(15),
            BigInteger.of(10),
            BigInteger.of(0),
            BigInteger.of(1),
            BigInteger.of(7),
            BigInteger.of(8)
    )

    override val numbersUnderTestValues: List<Any> =  numbersUnderTestAsBigIntegers.asSequence()
            .map { it as Any }
            .toList()

}