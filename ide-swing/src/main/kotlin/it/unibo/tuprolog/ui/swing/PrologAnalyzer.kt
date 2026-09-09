package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tree.SemanticRole
import it.unibo.tuprolog.ui.gui.presentation.OperatorPresentation

/** Shared parser/lexer-backed classification for Swing editors. */
internal object PrologAnalyzer {
    private val lexer = PrologLexer.default()
    private val parser = PrologParser.default()

    fun analyze(
        source: String,
        presentedOperators: List<OperatorPresentation>,
    ): PrologAnalysis {
        if (source.isEmpty()) return PrologAnalysis(source, emptyList(), emptyList())
        val operators =
            (
                OperatorSet.DEFAULT.map { OperatorPresentation(it.functor, it.priority, it.specifier.name) } +
                    presentedOperators
            ).distinctBy { Triple(it.name, it.priority, it.specifier) }
        val lexed =
            try {
                lexer.lex(SourceText(source)).materialize()
            } catch (error: PrologSyntaxException) {
                return PrologAnalysis(source, emptyList(), listOf(error.toDiagnostic()))
            }
        val operatorNames = operators.mapTo(hashSetOf()) { it.name }
        val lexicalTokens =
            lexed.tokens.mapNotNull { token ->
                token.kind
                    .category(lexed.source.text(token.span), operatorNames)
                    ?.let { ColouredToken(token.span.start.offset, token.span.length, it) }
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
                return PrologAnalysis(source, lexicalTokens, listOf(error.toDiagnostic()))
            }
        return PrologAnalysis(
            source,
            lexed.tokens.mapNotNull { token ->
                (semantic[token.id]?.category ?: token.kind.category(lexed.source.text(token.span), operatorNames))
                    ?.let { ColouredToken(token.span.start.offset, token.span.length, it) }
            },
            emptyList(),
        )
    }

    private val SemanticRole.category: PrologCategory?
        get() =
            when (this) {
                SemanticRole.FUNCTOR -> PrologCategory.FUNCTOR
                SemanticRole.PREFIX_OPERATOR,
                SemanticRole.INFIX_OPERATOR,
                SemanticRole.POSTFIX_OPERATOR,
                SemanticRole.CUT,
                ->
                    PrologCategory.OPERATOR
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
                ->
                    PrologCategory.NUMBER
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
                if (spelling in
                    operatorNames
                ) {
                    PrologCategory.OPERATOR
                } else {
                    PrologCategory.ATOM
                }
            TokenKind.VARIABLE -> PrologCategory.VARIABLE
            TokenKind.DECIMAL_INTEGER, TokenKind.HEX_INTEGER, TokenKind.OCTAL_INTEGER, TokenKind.BINARY_INTEGER,
            TokenKind.FLOAT, TokenKind.CHARACTER_CODE,
            -> PrologCategory.NUMBER
            TokenKind.SINGLE_QUOTED_ATOM, TokenKind.DOUBLE_QUOTED_TEXT -> PrologCategory.STRING
            TokenKind.WHITESPACE, TokenKind.END_OF_INPUT -> null
            TokenKind.COMMA, TokenKind.PIPE, TokenKind.CUT, TokenKind.SIGN -> PrologCategory.OPERATOR
            else -> PrologCategory.DELIMITER
        }
}
