package it.unibo.tuprolog.ui.gui.plp

import kotlin.math.roundToLong

private const val MICRO_PERCENT_PER_PERCENT = 1_000_000L

/** Formats [probability] (in `[0, 1]`) as a percentage with up to 6 decimal digits, trimming trailing zeros. */
fun formatProbabilityPercentage(probability: Double): String {
    val microPercent = (probability * 100.0 * MICRO_PERCENT_PER_PERCENT).roundToLong()
    val whole = microPercent / MICRO_PERCENT_PER_PERCENT
    val fraction =
        (microPercent % MICRO_PERCENT_PER_PERCENT)
            .toString()
            .padStart(6, '0')
            .trimEnd('0')
    val text = if (fraction.isEmpty()) "$whole" else "$whole.$fraction"
    return "$text%"
}
