package it.unibo.tuprolog.parser.sources

/**
 * A possibly partial view of a source document.
 *
 * Offsets, lines, and columns are always relative to the complete document, even when only a
 * fragment of its text is retained.
 */
interface Source {
    /** Optional stable identifier used in diagnostics, such as a path or URI. */
    val id: String?

    /** First position whose text is retained by this view. */
    val start: SourcePosition

    /** First position after the text retained by this view. */
    val endExclusive: SourcePosition

    /**
     * Converts an absolute UTF-16 [offset] within the retained range to a position.
     *
     * @throws IllegalArgumentException if [offset] is outside the retained range
     */
    fun positionAt(offset: Int): SourcePosition

    /**
     * Returns text between the absolute, end-exclusive offsets.
     *
     * @throws IndexOutOfBoundsException if either offset is outside retained text
     */
    fun text(
        startOffset: Int = start.offset,
        endExclusiveOffset: Int = endExclusive.offset,
    ): String

    /**
     * Returns text covered by [span].
     *
     * @throws IndexOutOfBoundsException if [span] is outside retained text
     */
    fun text(span: SourceSpan): String = text(span.start.offset, span.endExclusive.offset)

    /**
     * Creates a span from two absolute offsets.
     *
     * @throws IllegalArgumentException if the end precedes the start or an offset is not retained
     */
    fun span(
        startOffset: Int,
        endExclusiveOffset: Int,
    ): SourceSpan {
        require(startOffset <= endExclusiveOffset) {
            "A source span cannot end before it starts"
        }
        return SourceSpan(positionAt(startOffset), positionAt(endExclusiveOffset))
    }
}
