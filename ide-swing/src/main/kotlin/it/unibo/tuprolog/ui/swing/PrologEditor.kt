package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.ui.gui.presentation.Diagnostic
import it.unibo.tuprolog.ui.gui.presentation.DiagnosticSeverity
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation
import it.unibo.tuprolog.ui.gui.presentation.TextPosition
import it.unibo.tuprolog.ui.gui.presentation.TextRange
import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import javax.swing.text.DefaultHighlighter
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants

/** Editable, syntax-coloured source area used for document tabs. */
internal class PrologEditor : JTextPane() {
    var diagnostics: List<Diagnostic> = emptyList()
        private set

    private var lastHighlight: Pair<String, List<OperatorPresentation>>? = null

    init {
        font = Font(Font.MONOSPACED, Font.PLAIN, 14)
    }

    fun highlight(operators: List<OperatorPresentation>) {
        val input = text
        val key = input to operators
        if (lastHighlight == key) return
        lastHighlight = key
        val analysis = PrologAnalyzer.analyze(input, operators)
        styledDocument.setCharacterAttributes(0, input.length, PrologStyles.plain, true)
        for (token in analysis.tokens) {
            styledDocument.setCharacterAttributes(token.start, token.length, PrologStyles.of(token.category), true)
        }
        diagnostics = analysis.diagnostics
        paintDiagnostics()
    }

    private fun paintDiagnostics() {
        highlighter.removeAllHighlights()
        val length = document.length
        for (diagnostic in diagnostics) {
            val range = diagnostic.range ?: continue
            val start = range.start.offset.coerceIn(0, length)
            val end =
                range.endExclusive.offset
                    .coerceAtLeast(start + 1)
                    .coerceAtMost(length)
            if (end <= start) continue
            runCatching { highlighter.addHighlight(start, end, PrologStyles.painterFor(diagnostic.severity)) }
        }
    }

    internal fun categoryAt(offset: Int): String? =
        PrologStyles.styles.entries
            .firstOrNull {
                styledDocument.getCharacterElement(offset).attributes.containsAttributes(it.value)
            }?.key
            ?.name
}

/** Single-line, syntax-coloured field used for the query bar. */
internal class PrologQueryField : JTextPane() {
    var onSubmit: (() -> Unit)? = null

    private var lastHighlight: Pair<String, List<OperatorPresentation>>? = null

    init {
        font = Font(Font.MONOSPACED, Font.PLAIN, 14)
        (document as? javax.swing.text.AbstractDocument)?.documentFilter = SingleLineFilter()
        getInputMap(WHEN_FOCUSED).put(javax.swing.KeyStroke.getKeyStroke("ENTER"), "submit-query")
        actionMap.put(
            "submit-query",
            object : javax.swing.AbstractAction() {
                override fun actionPerformed(event: java.awt.event.ActionEvent) {
                    onSubmit?.invoke()
                }
            },
        )
    }

    fun highlight(operators: List<OperatorPresentation>) {
        val input = text
        val key = input to operators
        if (lastHighlight == key) return
        lastHighlight = key
        val analysis = PrologAnalyzer.analyze(input, operators)
        styledDocument.setCharacterAttributes(0, input.length, PrologStyles.plain, true)
        for (token in analysis.tokens) {
            styledDocument.setCharacterAttributes(token.start, token.length, PrologStyles.of(token.category), true)
        }
    }

    private class SingleLineFilter : javax.swing.text.DocumentFilter() {
        override fun insertString(
            fb: FilterBypass,
            offset: Int,
            string: String,
            attr: javax.swing.text.AttributeSet?,
        ) = super.insertString(fb, offset, string.replace("\n", "").replace("\r", ""), attr)

        override fun replace(
            fb: FilterBypass,
            offset: Int,
            length: Int,
            text: String?,
            attrs: javax.swing.text.AttributeSet?,
        ) = super.replace(fb, offset, length, text?.replace("\n", "")?.replace("\r", "").orEmpty(), attrs)
    }
}

internal enum class PrologCategory {
    COMMENT,
    OPERATOR,
    DELIMITER,
    FUNCTOR,
    ATOM,
    VARIABLE,
    NUMBER,
    STRING,
}

internal object PrologStyles {
    val plain = style(Color(0x22, 0x22, 0x22))
    val styles =
        mapOf(
            PrologCategory.COMMENT to style(Color(0x6A, 0x73, 0x7D), italic = true),
            PrologCategory.OPERATOR to style(Color(0xA6, 0x26, 0xA4), bold = true),
            PrologCategory.DELIMITER to style(Color(0x55, 0x55, 0x55), bold = true),
            PrologCategory.FUNCTOR to style(Color(0x00, 0x5C, 0x99), bold = true),
            PrologCategory.ATOM to style(Color(0x00, 0x66, 0x00)),
            PrologCategory.VARIABLE to style(Color(0x9A, 0x67, 0x00)),
            PrologCategory.NUMBER to style(Color(0x00, 0x55, 0xAA)),
            PrologCategory.STRING to style(Color(0xA3, 0x15, 0x15)),
        )

    private val errorPainter = DefaultHighlighter.DefaultHighlightPainter(Color(0xFF, 0x00, 0x00, 60))
    private val warningPainter = DefaultHighlighter.DefaultHighlightPainter(Color(0xFF, 0xA5, 0x00, 60))
    private val infoPainter = DefaultHighlighter.DefaultHighlightPainter(Color(0x00, 0x90, 0xFF, 50))

    fun of(category: PrologCategory): SimpleAttributeSet = styles.getValue(category)

    fun painterFor(severity: DiagnosticSeverity): DefaultHighlighter.DefaultHighlightPainter =
        when (severity) {
            DiagnosticSeverity.ERROR -> errorPainter
            DiagnosticSeverity.WARNING -> warningPainter
            DiagnosticSeverity.INFO -> infoPainter
        }

    private fun style(
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

internal data class ColouredToken(
    val start: Int,
    val length: Int,
    val category: PrologCategory,
)

internal data class PrologAnalysis(
    val tokens: List<ColouredToken>,
    val diagnostics: List<Diagnostic>,
)

/** Shared lexer/parser-backed classification used by both the editor and the query field. */
internal object PrologAnalyzer {
    private val lexer = PrologLexer.default()
    private val parser = PrologParser.default()

    fun analyze(
        source: String,
        presentedOperators: List<OperatorPresentation>,
    ): PrologAnalysis {
        if (source.isEmpty()) return PrologAnalysis(emptyList(), emptyList())
        val operators =
            (
                OperatorSet.DEFAULT.map { OperatorPresentation(it.functor, it.priority, it.specifier.name) } +
                    presentedOperators
            ).distinctBy { Triple(it.name, it.priority, it.specifier) }
        val lexed =
            runCatching { lexer.lex(SourceText(source)).materialize() }.getOrNull()
                ?: return PrologAnalysis(emptyList(), emptyList())
        val operatorTable =
            OperatorTables.of(
                operators.mapNotNull {
                    runCatching {
                        OperatorDefinition(it.name, Associativity.valueOf(it.specifier.uppercase()), it.priority)
                    }.getOrNull()
                },
            )
        var diagnostics = emptyList<Diagnostic>()
        val semantic =
            runCatching {
                parser.parseTheory(lexed, operatorTable).semanticTokens.associate { it.tokenId to it.role }
            }.getOrElse { error ->
                diagnostics = listOfNotNull((error as? PrologSyntaxException)?.toDiagnostic())
                emptyMap()
            }
        val operatorNames = operators.mapTo(hashSetOf()) { it.name }
        val tokens =
            lexed.tokens.mapNotNull { token ->
                val category =
                    semantic[token.id]?.category ?: token.kind.category(lexed.source.text(token.span), operatorNames)
                category?.let { ColouredToken(token.span.start.offset, token.span.length, it) }
            }
        return PrologAnalysis(tokens, diagnostics)
    }

    private fun PrologSyntaxException.toDiagnostic(): Diagnostic =
        Diagnostic(
            severity = DiagnosticSeverity.ERROR,
            message = message ?: "Syntax error",
            range = TextRange(span.start.toTextPosition(), span.endExclusive.toTextPosition()),
        )

    private fun SourcePosition.toTextPosition(): TextPosition = TextPosition(offset, line, column)

    private val SemanticRole.category: PrologCategory?
        get() =
            when (this) {
                SemanticRole.FUNCTOR -> PrologCategory.FUNCTOR
                SemanticRole.PREFIX_OPERATOR,
                SemanticRole.INFIX_OPERATOR,
                SemanticRole.POSTFIX_OPERATOR,
                SemanticRole.CUT,
                -> PrologCategory.OPERATOR
                SemanticRole.ATOM,
                SemanticRole.QUOTED_ATOM,
                SemanticRole.TRUTH_VALUE,
                -> PrologCategory.ATOM
                SemanticRole.VARIABLE,
                SemanticRole.ANONYMOUS_VARIABLE,
                -> PrologCategory.VARIABLE
                SemanticRole.INTEGER_LITERAL,
                SemanticRole.REAL_LITERAL,
                SemanticRole.CHARACTER_LITERAL,
                SemanticRole.NUMBER_SIGN,
                -> PrologCategory.NUMBER
                SemanticRole.DOUBLE_QUOTED_TEXT -> PrologCategory.STRING
                else -> PrologCategory.DELIMITER
            }

    private fun TokenKind.category(
        spelling: String,
        operatorNames: Set<String>,
    ): PrologCategory? =
        when (this) {
            TokenKind.LINE_COMMENT, TokenKind.BLOCK_COMMENT -> PrologCategory.COMMENT
            TokenKind.WORD_ATOM, TokenKind.GRAPHIC_ATOM ->
                if (spelling in operatorNames) PrologCategory.OPERATOR else PrologCategory.ATOM
            TokenKind.VARIABLE -> PrologCategory.VARIABLE
            TokenKind.DECIMAL_INTEGER,
            TokenKind.HEX_INTEGER,
            TokenKind.OCTAL_INTEGER,
            TokenKind.BINARY_INTEGER,
            TokenKind.FLOAT,
            TokenKind.CHARACTER_CODE,
            -> PrologCategory.NUMBER
            TokenKind.SINGLE_QUOTED_ATOM, TokenKind.DOUBLE_QUOTED_TEXT -> PrologCategory.STRING
            TokenKind.WHITESPACE, TokenKind.END_OF_INPUT -> null
            TokenKind.COMMA, TokenKind.PIPE, TokenKind.CUT, TokenKind.SIGN -> PrologCategory.OPERATOR
            else -> PrologCategory.DELIMITER
        }
}
