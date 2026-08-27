package it.unibo.tuprolog.parser.sources

/** End-exclusive source range. */
data class SourceSpan(
    val start: SourcePosition,
    val endExclusive: SourcePosition,
) {
    init {
        require(start.offset <= endExclusive.offset) {
            "A source span cannot end before it starts"
        }
    }

    val length: Int
        get() = endExclusive.offset - start.offset
}
