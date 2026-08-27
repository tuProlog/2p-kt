package it.unibo.tuprolog.parser.sources

/** Immutable source text together with an optional diagnostic identifier. */
class SourceText(
    val text: String,
    val id: String? = null,
) {
    private val lineStarts: IntArray by lazy { computeLineStarts(text) }

    /** Converts a UTF-16 string offset into a zero-based line and column. */
    fun positionAt(offset: Int): SourcePosition {
        require(offset in 0..text.length) {
            "Offset $offset is outside source range 0..${text.length}"
        }

        var low = 0
        var high = lineStarts.lastIndex
        while (low <= high) {
            val middle = (low + high) ushr 1
            if (lineStarts[middle] <= offset) {
                low = middle + 1
            } else {
                high = middle - 1
            }
        }
        val line = high.coerceAtLeast(0)
        return SourcePosition(offset, line, offset - lineStarts[line])
    }

    fun span(
        startOffset: Int,
        endExclusiveOffset: Int,
    ): SourceSpan {
        require(startOffset <= endExclusiveOffset) {
            "A source span cannot end before it starts"
        }
        return SourceSpan(positionAt(startOffset), positionAt(endExclusiveOffset))
    }

    override fun equals(other: Any?): Boolean = other is SourceText && text == other.text && id == other.id

    override fun hashCode(): Int = 31 * text.hashCode() + (id?.hashCode() ?: 0)

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
