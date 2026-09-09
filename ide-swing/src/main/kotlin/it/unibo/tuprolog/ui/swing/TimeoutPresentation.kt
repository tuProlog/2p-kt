package it.unibo.tuprolog.ui.swing

internal fun timeoutLabel(milliseconds: Long): String {
    if (milliseconds == 0L) return "Timeout: no limit"
    var remaining = milliseconds
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
