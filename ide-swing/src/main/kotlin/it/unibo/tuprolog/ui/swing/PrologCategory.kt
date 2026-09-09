package it.unibo.tuprolog.ui.swing

import org.fife.ui.rsyntaxtextarea.TokenTypes

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

internal fun PrologCategory.tokenType(): Int =
    when (this) {
        PrologCategory.COMMENT -> TokenTypes.COMMENT_EOL
        PrologCategory.OPERATOR -> TokenTypes.OPERATOR
        PrologCategory.DELIMITER -> TokenTypes.SEPARATOR
        PrologCategory.FUNCTOR -> TokenTypes.FUNCTION
        PrologCategory.ATOM -> TokenTypes.IDENTIFIER
        PrologCategory.VARIABLE -> TokenTypes.VARIABLE
        PrologCategory.NUMBER -> TokenTypes.LITERAL_NUMBER_DECIMAL_INT
        PrologCategory.STRING -> TokenTypes.LITERAL_STRING_DOUBLE_QUOTE
    }

internal fun Int.toPrologCategory(): PrologCategory? =
    when (this) {
        TokenTypes.COMMENT_EOL, TokenTypes.COMMENT_MULTILINE -> PrologCategory.COMMENT
        TokenTypes.OPERATOR -> PrologCategory.OPERATOR
        TokenTypes.SEPARATOR -> PrologCategory.DELIMITER
        TokenTypes.FUNCTION -> PrologCategory.FUNCTOR
        TokenTypes.IDENTIFIER -> PrologCategory.ATOM
        TokenTypes.VARIABLE -> PrologCategory.VARIABLE
        TokenTypes.LITERAL_NUMBER_DECIMAL_INT -> PrologCategory.NUMBER
        TokenTypes.LITERAL_STRING_DOUBLE_QUOTE -> PrologCategory.STRING
        else -> null
    }
