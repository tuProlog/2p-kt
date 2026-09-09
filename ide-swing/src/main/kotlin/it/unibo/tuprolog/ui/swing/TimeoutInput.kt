package it.unibo.tuprolog.ui.swing

private const val MAX_TIMEOUT_MILLISECONDS = 604_800_000L
private val TIMEOUT_INPUT_REGEX =
    Regex("""^\s*(?:none|0|(?:\d+\s*(?:ms|s|m|h|d|w)\s*)+)\s*$""", RegexOption.IGNORE_CASE)
private val TIMEOUT_TOKEN_REGEX = Regex("""(\d+)\s*(ms|s|m|h|d|w)""", RegexOption.IGNORE_CASE)

internal fun parseTimeoutInput(input: String): Long? {
    if (!TIMEOUT_INPUT_REGEX.matches(input)) return null
    if (input.trim().equals("none", ignoreCase = true) || input.trim() == "0") return 0L
    val units =
        mapOf(
            "ms" to 1L,
            "s" to 1_000L,
            "m" to 60_000L,
            "h" to 3_600_000L,
            "d" to 86_400_000L,
            "w" to MAX_TIMEOUT_MILLISECONDS,
        )
    val matches = TIMEOUT_TOKEN_REGEX.findAll(input).toList()
    if (matches.sumOf { it.value.length } != input.count { !it.isWhitespace() }) return null
    if (matches.map { it.groupValues[2].lowercase() }.distinct().size != matches.size) return null
    val total =
        matches.fold(0L) { sum, match ->
            runCatching {
                Math.addExact(
                    sum,
                    Math.multiplyExact(match.groupValues[1].toLong(), units.getValue(match.groupValues[2].lowercase())),
                )
            }.getOrElse { return null }
        }
    return total.takeIf { it <= MAX_TIMEOUT_MILLISECONDS }
}

internal fun formatTimeoutInput(milliseconds: Long): String {
    if (milliseconds <= 0L) return "none"
    var remaining = milliseconds
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
