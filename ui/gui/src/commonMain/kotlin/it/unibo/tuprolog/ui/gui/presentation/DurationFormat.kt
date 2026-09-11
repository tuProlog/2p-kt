package it.unibo.tuprolog.ui.gui.presentation

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private const val MAX_TIMEOUT_MILLISECONDS = 604_800_000L // one week
private val DURATION_INPUT_REGEX =
    Regex("""^\s*(?:none|0|(?:\d+\s*(?:ms|s|m|h|d|w)\s*)+)\s*$""", RegexOption.IGNORE_CASE)
private val DURATION_TOKEN_REGEX = Regex("""(\d+)\s*(ms|s|m|h|d|w)""", RegexOption.IGNORE_CASE)
private val DURATION_UNIT_MILLISECONDS =
    mapOf(
        "ms" to 1L,
        "s" to 1_000L,
        "m" to 60_000L,
        "h" to 3_600_000L,
        "d" to 86_400_000L,
        "w" to MAX_TIMEOUT_MILLISECONDS,
    )

/**
 * Parses a human-readable duration such as "1h 30m" or "500ms", or "none"/"0" for no limit.
 * Returns `null` for malformed input, repeated units, or a total exceeding one week.
 */
fun parseDurationInput(input: String): Duration? {
    if (!DURATION_INPUT_REGEX.matches(input)) return null
    if (input.trim().equals("none", ignoreCase = true) || input.trim() == "0") return Duration.ZERO
    val matches = DURATION_TOKEN_REGEX.findAll(input).toList()
    if (matches.sumOf { it.value.length } != input.count { !it.isWhitespace() }) return null
    if (matches.map { it.groupValues[2].lowercase() }.distinct().size != matches.size) return null
    val total =
        matches.fold(0L) { sum, match ->
            val amount = match.groupValues[1].toLongOrNull() ?: return null
            val unitMillis = DURATION_UNIT_MILLISECONDS.getValue(match.groupValues[2].lowercase())
            if (unitMillis != 0L && amount > MAX_TIMEOUT_MILLISECONDS / unitMillis) return null
            val next = sum + amount * unitMillis
            if (next > MAX_TIMEOUT_MILLISECONDS) return null
            next
        }
    return total.milliseconds
}

/** Formats [duration] back into the compact input format accepted by [parseDurationInput]. */
fun formatDurationInput(duration: Duration): String {
    var remaining = duration.inWholeMilliseconds
    if (remaining <= 0L) return "none"
    return listOf(
        "w" to MAX_TIMEOUT_MILLISECONDS,
        "d" to 86_400_000L,
        "h" to 3_600_000L,
        "m" to 60_000L,
        "s" to 1_000L,
        "ms" to 1L,
    ).mapNotNull { (unit, size) ->
        (remaining / size).takeIf { it > 0 }?.also { remaining %= size }?.let { "$it$unit" }
    }.joinToString(" ")
}

/** Human-readable label such as "Timeout: 1 hour 30 minutes", for status displays. */
fun durationLabel(duration: Duration): String {
    var remaining = duration.inWholeMilliseconds
    if (remaining == 0L) return "Timeout: no limit"
    val units =
        listOf(
            "week" to 604_800_000L,
            "day" to 86_400_000L,
            "hour" to 3_600_000L,
            "minute" to 60_000L,
            "second" to 1_000L,
        )
    val parts =
        units
            .mapNotNull { (name, size) ->
                (remaining / size)
                    .takeIf {
                        it > 0
                    }?.also { remaining %= size }
                    ?.let { "$it $name${if (it == 1L) "" else "s"}" }
            }.toMutableList()
    if (remaining > 0 || parts.isEmpty()) parts += "$remaining ms"
    return "Timeout: ${parts.joinToString(" ")}"
}
