package it.unibo.tuprolog.ui.gui.presentation

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tree.SemanticRole

/** Result of analysing [source]: its classified tokens and any syntax diagnostics found along the way. */
data class SyntaxAnalysis(
    val source: String,
    val tokens: List<SemanticToken>,
    val diagnostics: List<Diagnostic>,
)

/** Parser/lexer-backed classification shared by every Prolog-editing frontend (Swing, Compose, Web, ...). */
object PrologSyntaxAnalyzer {
    private val lexer = PrologLexer.default()
    private val parser = PrologParser.default()

    fun analyze(
        source: String,
        presentedOperators: List<OperatorPresentation>,
    ): SyntaxAnalysis {
        if (source.isEmpty()) return SyntaxAnalysis(source, emptyList(), emptyList())
        val operators =
            (
                OperatorSet.DEFAULT.map { OperatorPresentation(it.functor, it.priority, it.specifier.name) } +
                    presentedOperators
            ).distinctBy { Triple(it.name, it.priority, it.specifier) }
        val lexed =
            try {
                lexer.lex(SourceText(source)).materialize()
            } catch (error: PrologSyntaxException) {
                return SyntaxAnalysis(source, emptyList(), listOf(error.toDiagnostic()))
            }
        val operatorNames = operators.mapTo(hashSetOf()) { it.name }
        val lexicalTokens =
            lexed.tokens.mapNotNull { token ->
                token.kind
                    .category(lexed.source.text(token.span), operatorNames)
                    ?.let { SemanticToken(token.span.toTextRange(), it) }
            }
        val operatorTable =
            OperatorTables.of(
                operators.mapNotNull {
                    runCatching {
                        OperatorDefinition(it.name, Associativity.valueOf(it.specifier.uppercase()), it.priority)
                    }.getOrNull()
                },
            )
        val semantic =
            try {
                parser.parseTheory(lexed, operatorTable).semanticTokens.associate { it.tokenId to it.role }
            } catch (error: PrologSyntaxException) {
                return SyntaxAnalysis(source, lexicalTokens, listOf(error.toDiagnostic()))
            }
        return SyntaxAnalysis(
            source,
            lexed.tokens.mapNotNull { token ->
                (semantic[token.id]?.category ?: token.kind.category(lexed.source.text(token.span), operatorNames))
                    ?.let { SemanticToken(token.span.toTextRange(), it) }
            },
            emptyList(),
        )
    }

    private val SemanticRole.category: SemanticCategory?
        get() =
            when (this) {
                SemanticRole.FUNCTOR -> SemanticCategory.FUNCTOR
                SemanticRole.PREFIX_OPERATOR,
                SemanticRole.INFIX_OPERATOR,
                SemanticRole.POSTFIX_OPERATOR,
                SemanticRole.CUT,
                -> SemanticCategory.OPERATOR
                SemanticRole.ATOM,
                SemanticRole.QUOTED_ATOM,
                SemanticRole.TRUTH_VALUE,
                -> SemanticCategory.ATOM
                SemanticRole.VARIABLE,
                SemanticRole.ANONYMOUS_VARIABLE,
                -> SemanticCategory.VARIABLE
                SemanticRole.INTEGER_LITERAL,
                SemanticRole.REAL_LITERAL,
                SemanticRole.CHARACTER_LITERAL,
                SemanticRole.NUMBER_SIGN,
                -> SemanticCategory.NUMBER
                SemanticRole.DOUBLE_QUOTED_TEXT -> SemanticCategory.STRING
                SemanticRole.PARENTHESIS -> SemanticCategory.PARENTHESIS
                SemanticRole.CLAUSE_TERMINATOR -> SemanticCategory.FULL_STOP
                SemanticRole.BLOCK_DELIMITER -> SemanticCategory.BRACKET
                SemanticRole.LIST_DELIMITER -> SemanticCategory.BRACE
                SemanticRole.ARGUMENT_DELIMITER,
                SemanticRole.LIST_TAIL_DELIMITER,
                -> SemanticCategory.OPERATOR
            }

    private fun TokenKind.category(
        spelling: String,
        operatorNames: Set<String>,
    ): SemanticCategory? =
        when (this) {
            TokenKind.LINE_COMMENT, TokenKind.BLOCK_COMMENT -> SemanticCategory.COMMENT
            TokenKind.WORD_ATOM, TokenKind.GRAPHIC_ATOM ->
                if (spelling in
                    operatorNames
                ) {
                    SemanticCategory.OPERATOR
                } else {
                    SemanticCategory.ATOM
                }
            TokenKind.VARIABLE -> SemanticCategory.VARIABLE
            TokenKind.DECIMAL_INTEGER, TokenKind.HEX_INTEGER, TokenKind.OCTAL_INTEGER, TokenKind.BINARY_INTEGER,
            TokenKind.FLOAT, TokenKind.CHARACTER_CODE,
            -> SemanticCategory.NUMBER
            TokenKind.SINGLE_QUOTED_ATOM, TokenKind.DOUBLE_QUOTED_TEXT -> SemanticCategory.STRING
            TokenKind.WHITESPACE, TokenKind.END_OF_INPUT -> null
            TokenKind.COMMA, TokenKind.PIPE, TokenKind.CUT, TokenKind.SIGN -> SemanticCategory.OPERATOR
            TokenKind.LEFT_PARENTHESIS, TokenKind.RIGHT_PARENTHESIS -> SemanticCategory.PARENTHESIS
            TokenKind.LEFT_BRACKET, TokenKind.RIGHT_BRACKET -> SemanticCategory.BRACKET
            TokenKind.LEFT_BRACE, TokenKind.RIGHT_BRACE -> SemanticCategory.BRACE
            TokenKind.FULL_STOP -> SemanticCategory.FULL_STOP
        }
}

private fun SourceSpan.toTextRange(): TextRange = TextRange(start.toTextPosition(), endExclusive.toTextPosition())

private fun SourcePosition.toTextPosition(): TextPosition = TextPosition(offset, line, column)

internal fun PrologSyntaxException.toDiagnostic(): Diagnostic =
    Diagnostic(
        severity = DiagnosticSeverity.ERROR,
        message = message ?: "Syntax error",
        range = span.toTextRange(),
        source = DiagnosticSources.SYNTAX,
    )

/** A candidate identifier to offer for auto-completion, derived from already-classified [SemanticToken]s. */
data class SourceSuggestion(
    val text: String,
    val category: String,
)

/** Ranks identifiers already present in [analysis] as completion candidates, most recent first. */
fun sourceIdentifierSuggestions(analysis: SyntaxAnalysis): List<SourceSuggestion> =
    analysis.tokens
        .asReversed()
        .asSequence()
        .filter { it.category in setOf(SemanticCategory.VARIABLE, SemanticCategory.FUNCTOR, SemanticCategory.ATOM) }
        .map { token ->
            SourceSuggestion(
                analysis.source.substring(token.range.start.offset, token.range.endExclusive.offset),
                token.category.name.lowercase(),
            )
        }.distinctBy(SourceSuggestion::text)
        .toList()
