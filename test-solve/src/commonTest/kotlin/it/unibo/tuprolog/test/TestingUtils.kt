package it.unibo.tuprolog.test

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import org.gciatto.kt.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val DEFAULT_THRESHOLD = BigDecimal.of("1E-10")

fun assertAlmostEquals(
    expected: BigDecimal,
    actual: BigDecimal,
    threshold: BigDecimal = DEFAULT_THRESHOLD
) {
    val diff = (expected - actual).absoluteValue
    val tolerance = threshold.absoluteValue
    assertTrue(
        diff <= tolerance,
        message = "expected:<$expected> but was:<$actual>, while " +
            "the difference ($diff) is lower than the tolerance ($tolerance)"
    )
}

fun assertAlmostEquals(
    expected: Numeric,
    actual: Numeric,
    threshold: BigDecimal = DEFAULT_THRESHOLD
) = assertAlmostEquals(expected.decimalValue, actual.decimalValue, threshold)

fun assertAlmostEquals(
    expected: Real,
    actual: Real,
    threshold: BigDecimal = DEFAULT_THRESHOLD
) = assertAlmostEquals(expected.castToNumeric(), actual, threshold)

@Suppress("UNUSED_PARAMETER")
fun assertAlmostEquals(
    expected: Integer,
    actual: Integer,
    threshold: BigDecimal = DEFAULT_THRESHOLD
) = assertEquals(expected, actual)
