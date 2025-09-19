package it.unibo.tuprolog.utils

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

class NumberTypeTester {
    companion object {
        private val INT_REGEX = "[0-9]+".toRegex()
    }

    private val numberCache: Cache<Any, String> = Cache.simpleLru(8)

    val Number.isInteger: Boolean
        get() {
            return INT_REGEX.matches(stringRepresentation)
        }

    private val Number.stringRepresentation
        get() = numberCache.getOrSet(this) { toString() }

    fun Number.toInteger(): BigInteger = BigInteger.of(stringRepresentation)

    fun Number.toDecimal(): BigDecimal = BigDecimal.of(stringRepresentation)

    fun numberIsInteger(number: Number): Boolean = number.isInteger

    fun numberToInteger(number: Number): BigInteger = number.toInteger()

    fun numberToDecimal(number: Number): BigDecimal = number.toDecimal()
}
