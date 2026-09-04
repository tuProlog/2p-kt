package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.sources.Source
import it.unibo.tuprolog.parser.sources.SourcePosition

internal class BufferedSource(
    override val id: String?,
    origin: SourcePosition = SourcePosition(0, 0, 0),
) : Source {
    internal val content: StringBuilder = StringBuilder()

    private var basePosition: SourcePosition = origin

    var isFinished: Boolean = false
        private set

    override val start: SourcePosition
        get() = basePosition

    override val endExclusive: SourcePosition
        get() = positionAt(endOffset)

    val baseOffset: Int
        get() = basePosition.offset

    val endOffset: Int
        get() = baseOffset + content.length

    fun append(chunk: String) {
        check(!isFinished) { "Cannot append text after end of input" }
        content.append(chunk)
    }

    fun finish() {
        isFinished = true
    }

    fun charAtOrNull(offset: Int): Char? =
        if (offset in baseOffset until endOffset) content[offset - baseOffset] else null

    fun startsWith(
        value: String,
        offset: Int,
    ): Boolean {
        val local = offset - baseOffset
        if (local < 0 || local + value.length > content.length) {
            return false
        }
        for (index in value.indices) {
            if (content[local + index] != value[index]) {
                return false
            }
        }
        return true
    }

    override fun positionAt(offset: Int): SourcePosition {
        require(offset in baseOffset..endOffset) {
            "Offset $offset is outside retained source range $baseOffset..$endOffset"
        }
        return basePosition.advancePosition(content, 0, offset - baseOffset)
    }

    override fun text(
        startOffset: Int,
        endExclusiveOffset: Int,
    ): String {
        require(startOffset in baseOffset..endOffset && endExclusiveOffset in startOffset..endOffset) {
            "Source range $startOffset..$endExclusiveOffset is outside retained range $baseOffset..$endOffset"
        }
        return content.substring(startOffset - baseOffset, endExclusiveOffset - baseOffset)
    }

    fun discardBefore(offset: Int) {
        require(offset in baseOffset..endOffset)
        val count = offset - baseOffset
        if (count == 0) {
            return
        }
        basePosition = basePosition.advancePosition(content, 0, count)
        content.deleteRange(0, count)
    }
}
