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
    /** Map from a real number in string format to it's corresponding BigDecimal instance */
    private val stringToRealCorrectnessMap by lazy {
        mapOf(
            "0.0" to BigDecimal.ZERO,
            "1.0" to BigDecimal.ONE,
            "2.0" to BigDecimal.of(2.0),
            "2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427427466391932003059921817413596629043572900334295260595630738132328627943490763233829880753195251019011573834187930702154089149934884167509244761460668082264" to
                BigDecimal.E,
            "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648" to
                BigDecimal.PI,
            "0.5" to BigDecimal.ONE_HALF,
            "0.1" to BigDecimal.ONE_TENTH,
            "0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001" to
                BigDecimal.of("0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001"),
            "-1.0" to -BigDecimal.ONE,
            "-2.0" to -BigDecimal.of(2.0),
            "-2.718281828459045235360287471352662497757247093699959574966967627724076630353547594571382178525166427427466391932003059921817413596629043572900334295260595630738132328627943490763233829880753195251019011573834187930702154089149934884167509244761460668082264" to
                -BigDecimal.E,
            "-3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481117450284102701938521105559644622948954930381964428810975665933446128475648233786783165271201909145648" to
                -BigDecimal.PI,
            "-0.5" to -BigDecimal.ONE_HALF,
            "-0.1" to -BigDecimal.ONE_TENTH,
            "-0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001" to
                -BigDecimal.of(
                    "0.000000000000000000000000000000000000000000000000000000000000000000000000000000000001",
                ),
            Float.MAX_VALUE.toString() to BigDecimal.of(Float.MAX_VALUE.toString()),
            Float.MIN_VALUE.toString() to BigDecimal.of(Float.MIN_VALUE.toString()),
            Double.MAX_VALUE.toString() to BigDecimal.of(Double.MAX_VALUE.toString()),
            Double.MIN_VALUE.toString() to BigDecimal.of(Double.MIN_VALUE.toString()),
            "-" + Float.MAX_VALUE.toString() to -BigDecimal.of(Float.MAX_VALUE.toString()),
            "-" + Float.MIN_VALUE.toString() to -BigDecimal.of(Float.MIN_VALUE.toString()),
            "-" + Double.MAX_VALUE.toString() to -BigDecimal.of(Double.MAX_VALUE.toString()),
            "-" + Double.MIN_VALUE.toString() to -BigDecimal.of(Double.MIN_VALUE.toString()),
            "10.0" to BigDecimal.TEN,
            "10.000" to BigDecimal.TEN,
        )
    }

    /** Contains testing real values in string format */
    internal val stringNumbers by lazy { stringToRealCorrectnessMap.keys }

    /** Contains testing real values instances */
    internal val bigDecimals by lazy { stringToRealCorrectnessMap.values }

    /** Contains testing numbers represented as Double */
    internal val decimalsAsDoubles by lazy { bigDecimals.map { it.toDouble() } }

    /** Contains testing numbers that can be represented as Float (filtering out [Float.POSITIVE_INFINITY] and [Float.NEGATIVE_INFINITY]) */
    internal val decimalsAsFloats by lazy { bigDecimals.map { it.toFloat() }.filter { it.isFinite() } }
}
