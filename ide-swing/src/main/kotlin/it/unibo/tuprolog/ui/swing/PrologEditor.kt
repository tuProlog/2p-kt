package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

internal class PrologEditor : JTextPane() {
    private val plain = style(Color(0x22, 0x22, 0x22))
    private val styles =
        mapOf(
            Category.COMMENT to style(Color(0x6A, 0x73, 0x7D), italic = true),
            Category.OPERATOR to style(Color(0xA6, 0x26, 0xA4), bold = true),
            Category.DELIMITER to style(Color(0x55, 0x55, 0x55), bold = true),
            Category.FUNCTOR to style(Color(0x00, 0x5C, 0x99), bold = true),
            Category.ATOM to style(Color(0x00, 0x66, 0x00)),
            Category.VARIABLE to style(Color(0x9A, 0x67, 0x00)),
            Category.NUMBER to style(Color(0x00, 0x55, 0xAA)),
            Category.STRING to style(Color(0xA3, 0x15, 0x15)),
        )
    private var lastHighlight: Pair<String, List<OperatorPresentation>>? = null

    init {
        font = Font(Font.MONOSPACED, Font.PLAIN, 14)
    }

    fun highlight(operators: List<OperatorPresentation>) {
        val input = text
        val key = input to operators
        if (lastHighlight == key) return
        lastHighlight = key
        styledDocument.setCharacterAttributes(0, input.length, plain, true)
        for (token in PrologTokens.classify(input, operators)) {
            styledDocument.setCharacterAttributes(token.start, token.length, styles.getValue(token.category), true)
        }
    }

    internal fun categoryAt(offset: Int): String? =
        styles.entries
            .firstOrNull {
                styledDocument.getCharacterElement(offset).attributes.containsAttributes(it.value)
            }?.key
            ?.name

    private companion object {
        fun style(
            color: Color,
            bold: Boolean = false,
            italic: Boolean = false,
        ): SimpleAttributeSet =
            SimpleAttributeSet().apply {
                StyleConstants.setForeground(this, color)
                StyleConstants.setBold(this, bold)
                StyleConstants.setItalic(this, italic)
                StyleConstants.setFontFamily(this, Font.MONOSPACED)
                StyleConstants.setFontSize(this, 14)
            }
    }
}

private enum class Category {
    COMMENT,
    OPERATOR,
    DELIMITER,
    FUNCTOR,
    ATOM,
    VARIABLE,
    NUMBER,
    STRING,
}

private data class ColouredToken(
    val start: Int,
    val length: Int,
    val category: Category,
)

private object PrologTokens {
    private val lexer = PrologLexer.default()
    private val parser = PrologParser.default()

    fun classify(
        source: String,
        presentedOperators: List<OperatorPresentation>,
    ): List<ColouredToken> {
        if (source.isEmpty()) return emptyList()
        val operators =
            (
                OperatorSet.DEFAULT.map { OperatorPresentation(it.functor, it.priority, it.specifier.name) } +
                    presentedOperators
            ).distinctBy { Triple(it.name, it.priority, it.specifier) }
        val lexed = runCatching { lexer.lex(SourceText(source)).materialize() }.getOrNull() ?: return emptyList()
        val semantic =
            runCatching {
                parser
                    .parseTheory(
                        lexed,
                        OperatorTables.of(
                            operators.mapNotNull {
                                runCatching {
                                    OperatorDefinition(
                                        it.name,
                                        Associativity.valueOf(it.specifier.uppercase()),
                                        it.priority,
                                    )
                                }.getOrNull()
                            },
                        ),
                    ).semanticTokens
                    .associate { it.tokenId to it.role }
            }.getOrDefault(emptyMap())
        val operatorNames = operators.mapTo(hashSetOf()) { it.name }
        return lexed.tokens.mapNotNull { token ->
            val category =
                semantic[token.id]?.category ?: token.kind.category(lexed.source.text(token.span), operatorNames)
            category?.let { ColouredToken(token.span.start.offset, token.span.length, it) }
        }
    }

    private val SemanticRole.category: Category?
        get() =
            when (this) {
                SemanticRole.FUNCTOR -> Category.FUNCTOR
                SemanticRole.PREFIX_OPERATOR,
                SemanticRole.INFIX_OPERATOR,
                SemanticRole.POSTFIX_OPERATOR,
                SemanticRole.CUT,
                -> Category.OPERATOR
                SemanticRole.ATOM,
                SemanticRole.QUOTED_ATOM,
                SemanticRole.TRUTH_VALUE,
                -> Category.ATOM
                SemanticRole.VARIABLE,
                SemanticRole.ANONYMOUS_VARIABLE,
                -> Category.VARIABLE
                SemanticRole.INTEGER_LITERAL,
                SemanticRole.REAL_LITERAL,
                SemanticRole.CHARACTER_LITERAL,
                SemanticRole.NUMBER_SIGN,
                -> Category.NUMBER
                SemanticRole.DOUBLE_QUOTED_TEXT -> Category.STRING
                else -> Category.DELIMITER
            }

    private fun TokenKind.category(
        spelling: String,
        operatorNames: Set<String>,
    ): Category? =
        when (this) {
            TokenKind.LINE_COMMENT, TokenKind.BLOCK_COMMENT -> Category.COMMENT
            TokenKind.WORD_ATOM, TokenKind.GRAPHIC_ATOM ->
                if (spelling in operatorNames) Category.OPERATOR else Category.ATOM
            TokenKind.VARIABLE -> Category.VARIABLE
            TokenKind.DECIMAL_INTEGER,
            TokenKind.HEX_INTEGER,
            TokenKind.OCTAL_INTEGER,
            TokenKind.BINARY_INTEGER,
            TokenKind.FLOAT,
            TokenKind.CHARACTER_CODE,
            -> Category.NUMBER
            TokenKind.SINGLE_QUOTED_ATOM, TokenKind.DOUBLE_QUOTED_TEXT -> Category.STRING
            TokenKind.WHITESPACE, TokenKind.END_OF_INPUT -> null
            TokenKind.COMMA, TokenKind.PIPE, TokenKind.CUT, TokenKind.SIGN -> Category.OPERATOR
            else -> Category.DELIMITER
        }
}
