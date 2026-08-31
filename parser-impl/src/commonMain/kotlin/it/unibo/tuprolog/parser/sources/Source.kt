package it.unibo.tuprolog.parser.sources

/**
 * A possibly partial view of a source document.
 *
 * Offsets, lines, and columns are always relative to the complete document, even when only a
 * fragment of its text is retained.
 */
interface Source {
    val id: String?

    val start: SourcePosition

    val endExclusive: SourcePosition

    fun positionAt(offset: Int): SourcePosition

    fun text(
        startOffset: Int,
        endExclusiveOffset: Int,
    ): String

    fun text(span: SourceSpan): String = text(span.start.offset, span.endExclusive.offset)

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
