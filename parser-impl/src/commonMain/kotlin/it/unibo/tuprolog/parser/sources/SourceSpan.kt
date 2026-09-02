package it.unibo.tuprolog.parser.sources

/**
 * End-exclusive range in one source.
 *
 * @property start inclusive absolute position
 * @property endExclusive first position outside the range
 * @throws IllegalArgumentException if [endExclusive] precedes [start]
 */
data class SourceSpan(
    val start: SourcePosition,
    val endExclusive: SourcePosition,
) {
    init {
        require(start.offset <= endExclusive.offset) {
            "A source span cannot end before it starts"
        }
    }

    /** Length of the range in UTF-16 code units. */
    val length: Int
        get() = endExclusive.offset - start.offset
}
