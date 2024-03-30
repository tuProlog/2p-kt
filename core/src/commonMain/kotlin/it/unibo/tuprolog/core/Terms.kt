package it.unibo.tuprolog.core

import kotlin.jvm.JvmField

object Terms {
    private const val INT =
        """([0-9]+)"""

    private const val DEC =
        """(\.[0-9]+)"""

    private const val EXP =
        """([eE][+\-]?[0-9]+)"""

    @JvmField
    val VAR_NAME_PATTERN = "[A-Z_][A-Za-z_0-9]*".toRegex()

    @JvmField
    val WELL_FORMED_FUNCTOR_PATTERN =
        """^[a-z][A-Za-z_0-9]*$""".toRegex()

    @JvmField
    val ATOM_PATTERN = WELL_FORMED_FUNCTOR_PATTERN

    @JvmField
    val NON_PRINTABLE_CHARACTER_PATTERN =
        """[\t\n\r'"\\]""".toRegex()

    @JvmField
    val REAL_PATTERN = "^[+\\-]?(($INT$DEC$EXP?)|($INT$EXP)|($DEC$EXP?))$".toRegex()

    @JvmField
    val INTEGER_PATTERN =
        """^[+\-]?(0[xXbBoO])?[0-9A-Fa-f]+$""".toRegex()

    const val ANONYMOUS_VAR_NAME = "_"

    const val TUPLE_FUNCTOR = ","

    const val TRUE_FUNCTOR = "true"

    const val FALSE_FUNCTOR = "false"

    const val FAIL_FUNCTOR = "fail"

    const val BLOCK_FUNCTOR = "{}"

    const val CLAUSE_FUNCTOR = ":-"

    const val CONS_FUNCTOR = "."

    const val EMPTY_LIST_FUNCTOR = "[]"

    const val EMPTY_BLOCK_FUNCTOR = BLOCK_FUNCTOR

    const val INDICATOR_FUNCTOR = "/"

    @JvmField
    val NOTABLE_FUNCTORS_FOR_CLAUSES = listOf(TUPLE_FUNCTOR, ";", "->")

    fun escapeChar(
        char: Char,
        singleQuotes: Boolean = true,
        doubleQuotes: Boolean = !singleQuotes,
    ): String =
        when (char) {
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\r' -> "\\r"
            '\\' -> "\\\\"
            '\'' -> if (singleQuotes) "\\'" else "'"
            '\"' -> if (doubleQuotes) "\\\"" else "\""
            else -> "$char"
        }
}
