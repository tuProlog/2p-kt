package it.unibo.tuprolog.ui.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import it.unibo.tuprolog.ui.gui.presentation.SemanticToken
import it.unibo.tuprolog.ui.gui.presentation.TextPosition
import it.unibo.tuprolog.ui.gui.presentation.TextRange

class LineTokenSpanSpec :
    FunSpec({
        fun position(
            offset: Int,
            line: Int,
            column: Int,
        ) = TextPosition(offset, line, column)

        test("an empty line produces no spans") {
            semanticTokensForLine(0, "", emptyList()) shouldBe emptyList()
        }

        test("a line with no tokens is a single uncategorized span") {
            val spans = semanticTokensForLine(0, "p(1).", emptyList())
            spans shouldBe listOf(LineTokenSpan("p(1).", null))
        }

        test("tokens are surrounded by uncategorized gaps for whitespace and punctuation") {
            val line = "foo(X) :- bar(X)."
            val tokens =
                listOf(
                    SemanticToken(TextRange(position(0, 0, 0), position(3, 0, 3)), SemanticCategory.FUNCTOR),
                    SemanticToken(TextRange(position(4, 0, 4), position(5, 0, 5)), SemanticCategory.VARIABLE),
                    SemanticToken(TextRange(position(10, 0, 10), position(13, 0, 13)), SemanticCategory.FUNCTOR),
                )
            val spans = semanticTokensForLine(0, line, tokens)
            spans shouldBe
                listOf(
                    LineTokenSpan("foo", SemanticCategory.FUNCTOR),
                    LineTokenSpan("(", null),
                    LineTokenSpan("X", SemanticCategory.VARIABLE),
                    LineTokenSpan(") :- ", null),
                    LineTokenSpan("bar", SemanticCategory.FUNCTOR),
                    LineTokenSpan("(X).", null),
                )
        }

        test("a token spanning multiple lines is clipped to its portion of each line") {
            // "/* a\nb */" as a single BLOCK_COMMENT token from (0,0) to (1,4).
            val token = SemanticToken(TextRange(position(0, 0, 0), position(9, 1, 4)), SemanticCategory.COMMENT)

            semanticTokensForLine(0, "/* a", listOf(token)) shouldBe
                listOf(LineTokenSpan("/* a", SemanticCategory.COMMENT))
            semanticTokensForLine(1, "b */", listOf(token)) shouldBe
                listOf(LineTokenSpan("b */", SemanticCategory.COMMENT))
        }

        test("tokens outside the requested row are ignored") {
            val other = SemanticToken(TextRange(position(0, 5, 0), position(3, 5, 3)), SemanticCategory.ATOM)
            semanticTokensForLine(0, "p(1).", listOf(other)) shouldBe listOf(LineTokenSpan("p(1).", null))
        }
    })
