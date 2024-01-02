package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Integer
import org.gciatto.kt.math.BigInteger

/**
 * Utils singleton for testing [Integer]
 *
 * @author Enrico
 */
internal object IntegerUtils {
    /** Map from an integer in string format to it's corresponding BigInteger instance */
    private val stringToIntegerCorrectnessMap by lazy {
        mapOf(
            "0" to BigInteger.ZERO,
            "1" to BigInteger.ONE,
            "2" to BigInteger.TWO,
            "-1" to -BigInteger.ONE,
            "-2" to -BigInteger.TWO,
            Int.MAX_VALUE.toString() to BigInteger.of(Int.MAX_VALUE),
            Int.MIN_VALUE.toString() to BigInteger.of(Int.MIN_VALUE),
            Long.MAX_VALUE.toString() to BigInteger.of(Long.MAX_VALUE),
            Long.MIN_VALUE.toString() to BigInteger.of(Long.MIN_VALUE),
            "0xf" to BigInteger.of(15),
            "0XA" to BigInteger.of(10),
            "0b0" to BigInteger.of(0),
            "0B1" to BigInteger.of(1),
            "0o7" to BigInteger.of(7),
            "0O10" to BigInteger.of(8),
        )
    }

    /** Contains testing integer values in string format */
    internal val stringNumbers by lazy { stringToIntegerCorrectnessMap.keys }

    /** Contains testing integer values instances */
    internal val bigIntegers by lazy { stringToIntegerCorrectnessMap.values }

    /** Contains only numbers representable with Long */
    internal val onlyLongs by lazy {
        bigIntegers.mapNotNull {
            try {
                it.toLongExact()
            } catch (e: Exception) {
                null
            }
        }
    }

    /** Contains only numbers representable with Int */
    internal val onlyInts by lazy {
        bigIntegers.mapNotNull {
            try {
                it.toIntExact()
            } catch (e: Exception) {
                null
            }
        }
    }

    /** Contains only numbers representable with Short */
    internal val onlyShorts by lazy {
        bigIntegers.mapNotNull {
            try {
                it.toShortExact()
            } catch (e: Exception) {
                null
            }
        }
    }

    /** Contains only numbers representable with Byte */
    internal val onlyBytes by lazy {
        bigIntegers.mapNotNull {
            try {
                it.toByteExact()
            } catch (e: Exception) {
                null
            }
        }
    }
}
