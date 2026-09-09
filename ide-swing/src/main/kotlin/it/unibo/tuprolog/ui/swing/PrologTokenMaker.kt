package it.unibo.tuprolog.ui.swing

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker
import org.fife.ui.rsyntaxtextarea.Token
import org.fife.ui.rsyntaxtextarea.TokenMap
import org.fife.ui.rsyntaxtextarea.TokenTypes
import javax.swing.text.Segment
import kotlin.math.max
import kotlin.math.min

/** RSTA adapter that slices the whole-source parser analysis into its requested line. */
internal class PrologTokenMaker(
    private val analysis: () -> PrologAnalysis,
) : AbstractTokenMaker() {
    override fun getWordsToHighlight(): TokenMap = TokenMap()

    override fun getTokenList(
        text: Segment,
        initialTokenType: Int,
        startOffset: Int,
    ): Token {
        resetTokenList()
        val lineEnd = startOffset + text.count
        var cursor = 0
        for (token in analysis().tokens) {
            val start = max(token.start, startOffset)
            val end = min(token.start + token.length, lineEnd)
            if (start >= end) continue
            addGap(text, cursor, start - startOffset, startOffset)
            addRange(text, start - startOffset, end - startOffset, token.category.tokenType(), startOffset)
            cursor = end - startOffset
        }
        addGap(text, cursor, text.count, startOffset)
        addNullToken()
        return firstToken
    }

    override fun getLineCommentStartAndEnd(languageIndex: Int): Array<String?> = arrayOf("%", null)

    override fun isIdentifierChar(
        languageIndex: Int,
        ch: Char,
    ): Boolean = ch == '_' || ch.isLetterOrDigit()

    private fun addRange(
        text: Segment,
        start: Int,
        endExclusive: Int,
        tokenType: Int,
        documentStart: Int,
    ) {
        if (start <
            endExclusive
        ) {
            addToken(
                text.array,
                text.offset + start,
                text.offset + endExclusive - 1,
                tokenType,
                documentStart + start,
            )
        }
    }

    private fun addGap(
        text: Segment,
        start: Int,
        endExclusive: Int,
        documentStart: Int,
    ) {
        val type =
            if ((start until endExclusive).all { text.array[text.offset + it].isWhitespace() }) {
                TokenTypes.WHITESPACE
            } else {
                TokenTypes.IDENTIFIER
            }
        addRange(text, start, endExclusive, type, documentStart)
    }
}
