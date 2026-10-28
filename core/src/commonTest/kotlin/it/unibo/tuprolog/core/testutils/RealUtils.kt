package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Real
import org.gciatto.kt.math.BigDecimal

/**
 * Utils singleton for testing [Real]
 *
 * @author Enrico
 */
@Suppress("ktlint:standard:max-line-length")
internal object RealUtils {
    /** A named real number in string format, along with its corresponding BigDecimal instance */
    internal data class RealCase(
        val id: String,
        val string: String,
        val value: BigDecimal,
    ) {
        val double: Double get() = value.toDouble()

        /** The [value] as Float, if it can be represented (i.e. it is not [Float.POSITIVE_INFINITY] nor [Float.NEGATIVE_INFINITY]) */
        val float: Float? get() = value.toFloat().takeIf { it.isFinite() }
    }

    /** Contains testing real values, in string format and as BigDecimal instances */
    internal val realCases by lazy {
        listOf(
            RealCase("zero", "0.0", BigDecimal.ZERO),
            RealCase("one", "1.0", BigDecimal.ONE),
            RealCase("two", "2.0", BigDecimal.of(2.0)),
            RealCase(
                "e",
                "2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427427466391932003059921817413596629043572900334295260595630738132328627943490763233829880753195251019011573834187930702154089149934884167509244761460668082264",
                BigDecimal.E,
            ),
            RealCase(
                "pi",
                "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648",
                BigDecimal.PI,
            ),
            RealCase("one-half", "0.5", BigDecimal.ONE_HALF),
            RealCase("one-tenth", "0.1", BigDecimal.ONE_TENTH),
            RealCase(
                "tiny",
                "0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001",
                BigDecimal.of("0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001"),
            ),
            RealCase("minus-one", "-1.0", -BigDecimal.ONE),
            RealCase("minus-two", "-2.0", -BigDecimal.of(2.0)),
            RealCase(
                "minus-e",
                "-2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427427466391932003059921817413596629043572900334295260595630738132328627943490763233829880753195251019011573834187930702154089149934884167509244761460668082264",
                -BigDecimal.E,
            ),
            RealCase(
                "minus-pi",
                "-3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648",
                -BigDecimal.PI,
            ),
            RealCase("minus-one-half", "-0.5", -BigDecimal.ONE_HALF),
            RealCase("minus-one-tenth", "-0.1", -BigDecimal.ONE_TENTH),
            RealCase(
                "minus-tiny",
                "-0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001",
                -BigDecimal.of(
                    "0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001",
                ),
            ),
            RealCase("float-max", Float.MAX_VALUE.toString(), BigDecimal.of(Float.MAX_VALUE.toString())),
            RealCase("float-min", Float.MIN_VALUE.toString(), BigDecimal.of(Float.MIN_VALUE.toString())),
            RealCase("double-max", Double.MAX_VALUE.toString(), BigDecimal.of(Double.MAX_VALUE.toString())),
            RealCase("double-min", Double.MIN_VALUE.toString(), BigDecimal.of(Double.MIN_VALUE.toString())),
            RealCase("minus-float-max", "-" + Float.MAX_VALUE.toString(), -BigDecimal.of(Float.MAX_VALUE.toString())),
            RealCase("minus-float-min", "-" + Float.MIN_VALUE.toString(), -BigDecimal.of(Float.MIN_VALUE.toString())),
            RealCase(
                "minus-double-max",
                "-" + Double.MAX_VALUE.toString(),
                -BigDecimal.of(Double.MAX_VALUE.toString()),
            ),
            RealCase(
                "minus-double-min",
                "-" + Double.MIN_VALUE.toString(),
                -BigDecimal.of(Double.MIN_VALUE.toString()),
            ),
            RealCase("ten", "10.0", BigDecimal.TEN),
            RealCase("ten-with-trailing-zeros", "10.000", BigDecimal.TEN),
        )
    }
}
