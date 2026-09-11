package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken
import it.unibo.tuprolog.ui.gui.presentation.TextPosition
import it.unibo.tuprolog.ui.gui.presentation.TextRange
import kotlin.test.Test
import kotlin.test.assertEquals

class LineTokenSpanTest {
    private fun position(
        offset: Int,
        line: Int,
        column: Int,
    ) = TextPosition(offset, line, column)

    @Test
    fun `an empty line produces no spans`() {
        assertEquals(emptyList(), semanticTokensForLine(0, "", emptyList()))
    }

    @Test
    fun `a line with no tokens is a single uncategorized span`() {
        val spans = semanticTokensForLine(0, "p(1).", emptyList())
        assertEquals(listOf(LineTokenSpan("p(1).", null)), spans)
    }

    @Test
    fun `tokens are surrounded by uncategorized gaps for whitespace and punctuation`() {
        val line = "foo(X) :- bar(X)."
        val tokens =
            listOf(
                SemanticToken(TextRange(position(0, 0, 0), position(3, 0, 3)), SemanticCategory.FUNCTOR),
                SemanticToken(TextRange(position(4, 0, 4), position(5, 0, 5)), SemanticCategory.VARIABLE),
                SemanticToken(TextRange(position(10, 0, 10), position(13, 0, 13)), SemanticCategory.FUNCTOR),
            )
        val spans = semanticTokensForLine(0, line, tokens)
        assertEquals(
            listOf(
                LineTokenSpan("foo", SemanticCategory.FUNCTOR),
                LineTokenSpan("(", null),
                LineTokenSpan("X", SemanticCategory.VARIABLE),
                LineTokenSpan(") :- ", null),
                LineTokenSpan("bar", SemanticCategory.FUNCTOR),
                LineTokenSpan("(X).", null),
            ),
            spans,
        )
    }

    @Test
    fun `a token spanning multiple lines is clipped to its portion of each line`() {
        // "/* a\nb */" as a single BLOCK_COMMENT token from (0,0) to (1,4).
        val token = SemanticToken(TextRange(position(0, 0, 0), position(9, 1, 4)), SemanticCategory.COMMENT)

        assertEquals(
            listOf(LineTokenSpan("/* a", SemanticCategory.COMMENT)),
            semanticTokensForLine(0, "/* a", listOf(token)),
        )
        assertEquals(
            listOf(LineTokenSpan("b */", SemanticCategory.COMMENT)),
            semanticTokensForLine(1, "b */", listOf(token)),
        )
    }

    @Test
    fun `tokens outside the requested row are ignored`() {
        val other = SemanticToken(TextRange(position(0, 5, 0), position(3, 5, 3)), SemanticCategory.ATOM)
        assertEquals(listOf(LineTokenSpan("p(1).", null)), semanticTokensForLine(0, "p(1).", listOf(other)))
    }
}
