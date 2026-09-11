package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.presentation.SemanticCategory
import org.fife.ui.rsyntaxtextarea.TokenTypes

internal fun SemanticCategory.tokenType(): Int =
    when (this) {
        SemanticCategory.COMMENT -> TokenTypes.COMMENT_EOL
        SemanticCategory.OPERATOR -> TokenTypes.OPERATOR
        SemanticCategory.PARENTHESIS,
        SemanticCategory.BRACE,
        SemanticCategory.BRACKET,
        SemanticCategory.FULL_STOP,
        -> TokenTypes.SEPARATOR
        SemanticCategory.FUNCTOR -> TokenTypes.FUNCTION
        SemanticCategory.ATOM -> TokenTypes.IDENTIFIER
        SemanticCategory.VARIABLE -> TokenTypes.VARIABLE
        SemanticCategory.NUMBER -> TokenTypes.LITERAL_NUMBER_DECIMAL_INT
        SemanticCategory.STRING -> TokenTypes.LITERAL_STRING_DOUBLE_QUOTE
        SemanticCategory.DIRECTIVE -> TokenTypes.RESERVED_WORD
        SemanticCategory.ERROR -> TokenTypes.ERROR_IDENTIFIER
    }

internal fun Int.toSemanticCategory(): SemanticCategory? =
    when (this) {
        TokenTypes.COMMENT_EOL, TokenTypes.COMMENT_MULTILINE -> SemanticCategory.COMMENT
        TokenTypes.OPERATOR -> SemanticCategory.OPERATOR
        TokenTypes.SEPARATOR -> SemanticCategory.PARENTHESIS
        TokenTypes.FUNCTION -> SemanticCategory.FUNCTOR
        TokenTypes.IDENTIFIER -> SemanticCategory.ATOM
        TokenTypes.VARIABLE -> SemanticCategory.VARIABLE
        TokenTypes.LITERAL_NUMBER_DECIMAL_INT -> SemanticCategory.NUMBER
        TokenTypes.LITERAL_STRING_DOUBLE_QUOTE -> SemanticCategory.STRING
        else -> null
    }
