package it.unibo.tuprolog.parser.sources

/** A zero-based position in the complete input, measured in UTF-16 code units. */
data class SourcePosition(
    val offset: Int,
    val line: Int,
    val column: Int,
) {
    internal fun advancePosition(
        text: CharSequence,
        startIndex: Int = 0,
        endExclusiveIndex: Int = text.length,
    ): SourcePosition {
        var offset = offset
        var line = line
        var column = column
        var index = startIndex
        while (index < endExclusiveIndex) {
            when (text[index]) {
                '\r' -> {
                    if (index + 1 < endExclusiveIndex && text[index + 1] == '\n') {
                        index += 2
                        offset += 2
                    } else {
                        index += 1
                        offset += 1
                    }
                    line += 1
                    column = 0
                }
                '\n' -> {
                    index += 1
                    offset += 1
                    line += 1
                    column = 0
                }
                else -> {
                    index += 1
                    offset += 1
                    column += 1
                }
            }
        }
        return SourcePosition(offset, line, column)
    }
}
