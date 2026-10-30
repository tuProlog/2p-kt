package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Integer
import org.gciatto.kt.math.BigInteger

/**
 * Utils singleton for testing [Integer]
 *
 * @author Enrico
 */
internal object IntegerUtils {
    /** A named integer in string format, along with its corresponding BigInteger instance */
    internal data class IntegerCase(
        val id: String,
        val string: String,
        val value: BigInteger,
    ) {
        /** The [value] as Long, if representable */
        val long: Long? get() = exactOrNull { value.toLongExact() }

        /** The [value] as Int, if representable */
        val int: Int? get() = exactOrNull { value.toIntExact() }

        /** The [value] as Short, if representable */
        val short: Short? get() = exactOrNull { value.toShortExact() }

        /** The [value] as Byte, if representable */
        val byte: Byte? get() = exactOrNull { value.toByteExact() }

        private fun <T> exactOrNull(conversion: () -> T): T? =
            try {
                conversion()
            } catch (e: Exception) {
                null
            }
    }

    /** Contains testing integer values, in string format and as BigInteger instances */
    internal val integerCases by lazy {
        listOf(
            IntegerCase("zero", "0", BigInteger.ZERO),
            IntegerCase("one", "1", BigInteger.ONE),
            IntegerCase("two", "2", BigInteger.TWO),
            IntegerCase("minus-one", "-1", -BigInteger.ONE),
            IntegerCase("minus-two", "-2", -BigInteger.TWO),
            IntegerCase("int-max", Int.MAX_VALUE.toString(), BigInteger.of(Int.MAX_VALUE)),
            IntegerCase("int-min", Int.MIN_VALUE.toString(), BigInteger.of(Int.MIN_VALUE)),
            IntegerCase("long-max", Long.MAX_VALUE.toString(), BigInteger.of(Long.MAX_VALUE)),
            IntegerCase("long-min", Long.MIN_VALUE.toString(), BigInteger.of(Long.MIN_VALUE)),
            IntegerCase("hexadecimal-lowercase", "0xf", BigInteger.of(15)),
            IntegerCase("hexadecimal-uppercase", "0XA", BigInteger.of(10)),
            IntegerCase("binary-lowercase", "0b0", BigInteger.of(0)),
            IntegerCase("binary-uppercase", "0B1", BigInteger.of(1)),
            IntegerCase("octal-lowercase", "0o7", BigInteger.of(7)),
            IntegerCase("octal-uppercase", "0O10", BigInteger.of(8)),
        )
    }
}
