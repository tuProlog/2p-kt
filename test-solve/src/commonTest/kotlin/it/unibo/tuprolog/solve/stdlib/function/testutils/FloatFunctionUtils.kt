package it.unibo.tuprolog.solve.stdlib.function.testutils

import org.gciatto.kt.math.BigDecimal

/**
 * Utils singleton to help testing Float functions
 *
 * @author Enrico
 */
internal object FloatFunctionUtils {
    /** A named input number, with its expected integer and fractional parts */
    internal class FloatPartsCase(
        val id: String,
        input: Number,
        integerPart: Double,
        fractionalPart: Double,
    ) {
        val input: BigDecimal = BigDecimal.of(input.toString())
        val integerPart: BigDecimal = BigDecimal.of(integerPart.toString()).stripTrailingZeros()
        val fractionalPart: BigDecimal = BigDecimal.of(fractionalPart.toString()).stripTrailingZeros()
    }

    internal val floatPartsCases =
        listOf(
            FloatPartsCase("integer-one", 1, 1.0, 0.0),
            FloatPartsCase("real-one", 1.0, 1.0, 0.0),
            FloatPartsCase("one-tenth-above-one", 1.1, 1.0, 0.10),
            FloatPartsCase("many-decimals", 3.123456789101, 3.0, 0.123456789101),
            FloatPartsCase("tiny-fraction", 2.0000000001, 2.0, 0.0000000001),
            FloatPartsCase("negative-many-decimals", -3.123456789101, -3.0, -0.123456789101),
            FloatPartsCase("negative-tiny-fraction", -2.0000000001, -2.0, -0.0000000001),
            FloatPartsCase("large-negative", -1984984.02, -1984984.0, -0.02),
        )
}
