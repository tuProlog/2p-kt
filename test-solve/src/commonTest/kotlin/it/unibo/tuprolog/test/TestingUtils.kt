package it.unibo.tuprolog.test

import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import org.gciatto.kt.math.BigDecimal

private val DEFAULT_THRESHOLD = BigDecimal.of("1E-10")

fun assertAlmostEquals(
    expected: BigDecimal,
    actual: BigDecimal,
    threshold: BigDecimal = DEFAULT_THRESHOLD,
) {
    val diff = (expected - actual).absoluteValue
    val tolerance = threshold.absoluteValue
    withClue(
        "expected:<$expected> but was:<$actual>, while " +
            "the difference ($diff) is lower than the tolerance ($tolerance)",
    ) { (diff <= tolerance).shouldBeTrue() }
}

fun assertAlmostEquals(
    expected: Numeric,
    actual: Numeric,
    threshold: BigDecimal = DEFAULT_THRESHOLD,
) = assertAlmostEquals(expected.decimalValue, actual.decimalValue, threshold)

fun assertAlmostEquals(
    expected: Real,
    actual: Real,
    threshold: BigDecimal = DEFAULT_THRESHOLD,
) = assertAlmostEquals(expected.castToNumeric(), actual, threshold)

@Suppress("UNUSED_PARAMETER")
fun assertAlmostEquals(
    expected: Integer,
    actual: Integer,
    threshold: BigDecimal = DEFAULT_THRESHOLD,
) = actual shouldBe expected
