package it.unibo.tuprolog.parser.sources

/**
 * Immutable source text together with an optional diagnostic identifier.
 *
 * [origin] is non-zero for a materialized fragment of a streamed source. The [text] property then
 * contains only that fragment, while every position returned by this class remains absolute.
 *
 * ```kotlin
 * val fragment = SourceText("foo\nbar", "fragment", SourcePosition(100, 7, 11))
 * check(fragment.positionAt(104) == SourcePosition(104, 8, 0))
 * ```
 *
 * @property text complete retained fragment
 * @property id optional diagnostic source identifier
 * @property origin absolute position corresponding to `text[0]`
 */
class SourceText(
    val text: String,
    override val id: String? = null,
    val origin: SourcePosition = SourcePosition(0, 0, 0),
) : Source {
    private val lineStarts: IntArray by lazy { computeLineStarts(text) }

    override val start: SourcePosition
        get() = origin

    override val endExclusive: SourcePosition by lazy { positionAt(origin.offset + text.length) }

    /**
     * Converts an absolute UTF-16 string offset into a zero-based line and column.
     *
     * CRLF is treated as one line break; the position between its two code units is normalized to
     * the beginning of the following line.
     *
     * @throws IllegalArgumentException if [offset] is outside this retained fragment
     */
    override fun positionAt(offset: Int): SourcePosition {
        val relativeOffset = offset - origin.offset
        require(relativeOffset in 0..text.length) {
            "Offset $offset is outside source range ${origin.offset}..${origin.offset + text.length}"
        }

        var low = 0
        var high = lineStarts.lastIndex
        while (low <= high) {
            val middle = (low + high) ushr 1
            if (lineStarts[middle] <= relativeOffset) {
                low = middle + 1
            } else {
                high = middle - 1
            }
        }
        val relativeLine = high.coerceAtLeast(0)
        if (
            relativeOffset > 0 &&
            relativeOffset < text.length &&
            text[relativeOffset - 1] == '\r' &&
            text[relativeOffset] == '\n'
        ) {
            return SourcePosition(offset, origin.line + relativeLine + 1, 0)
        }
        val absoluteLine = origin.line + relativeLine
        val column =
            if (relativeLine == 0) {
                origin.column + relativeOffset
            } else {
                relativeOffset - lineStarts[relativeLine]
            }
        return SourcePosition(offset, absoluteLine, column)
    }

    /**
     * @throws IndexOutOfBoundsException if either offset is outside this retained fragment
     */
    override fun text(
        startOffset: Int,
        endExclusiveOffset: Int,
    ): String = text.substring(startOffset - origin.offset, endExclusiveOffset - origin.offset)

    /** Compares retained text, source identifier, and origin. */
    override fun equals(other: Any?): Boolean =
        other is SourceText && text == other.text && id == other.id && origin == other.origin

    /** Returns a hash derived from retained text, source identifier, and origin. */
    override fun hashCode(): Int = 31 * (31 * text.hashCode() + (id?.hashCode() ?: 0)) + origin.hashCode()

    /** Returns [id], or `<source>` when this source is anonymous. */
    override fun toString(): String = id ?: "<source>"

    private companion object {
        private fun computeLineStarts(text: String): IntArray {
            val starts = mutableListOf(0)
            var index = 0
            while (index < text.length) {
                when (text[index]) {
                    '\r' -> {
                        index += if (index + 1 < text.length && text[index + 1] == '\n') 2 else 1
                        starts += index
                    }
                    '\n' -> {
                        index += 1
                        starts += index
                    }
                    else -> index += 1
                }
            }
            return starts.toIntArray()
        }
    }
}
