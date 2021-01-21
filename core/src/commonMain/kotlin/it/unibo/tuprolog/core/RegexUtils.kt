package it.unibo.tuprolog.core

internal object RegexUtils {
    const val INT =
        """([0-9]+)"""

    const val DEC =
        """(\.[0-9]+)"""

    const val EXP =
        """([eE][+\-]?[0-9]+)"""

    fun escapeChar(
        char: Char,
        singleQuotes: Boolean = true,
        doubleQuotes: Boolean = !singleQuotes
    ): String {
        return when (char) {
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\r' -> "\\r"
            '\\' -> "\\\\"
            '\'' -> if (singleQuotes) "\\'" else "'"
            '\"' -> if (doubleQuotes) "\\\"" else "\""
            else -> "$char"
        }
    }
}
