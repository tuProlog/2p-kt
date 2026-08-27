package it.unibo.tuprolog.parser.sources

/** A zero-based position in the original input string. */
data class SourcePosition(
    val offset: Int,
    val line: Int,
    val column: Int,
)
