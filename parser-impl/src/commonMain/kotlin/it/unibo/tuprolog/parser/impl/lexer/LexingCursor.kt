package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.SourceText

internal class LexingCursor(
    val source: SourceText,
) {
    var offset: Int = 0
        private set

    private var line: Int = 0
    private var column: Int = 0

    val isAtEnd: Boolean
        get() = offset >= source.text.length

    fun current(): Char = source.text[offset]

    fun charAtOrNull(index: Int): Char? = source.text.getOrNull(index)

    fun startsWith(
        value: String,
        at: Int = offset,
    ): Boolean = source.text.startsWith(value, at)

    fun mark(): SourcePosition = SourcePosition(offset, line, column)

    fun advanceTo(targetOffset: Int) {
        require(targetOffset in offset..source.text.length)
        while (offset < targetOffset) {
            when (source.text[offset]) {
                '\r' -> {
                    if (offset + 1 < targetOffset && source.text[offset + 1] == '\n') {
                        offset += 2
                    } else {
                        offset += 1
                    }
                    line += 1
                    column = 0
                }
                '\n' -> {
                    offset += 1
                    line += 1
                    column = 0
                }
                else -> {
                    offset += 1
                    column += 1
                }
            }
        }
    }

    fun spanFrom(start: SourcePosition): SourceSpan = SourceSpan(start, mark())
}
