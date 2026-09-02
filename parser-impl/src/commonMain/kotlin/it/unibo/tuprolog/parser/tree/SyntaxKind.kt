package it.unibo.tuprolog.parser.tree

/** Stable structural category of a [SyntaxNode]. */
enum class SyntaxKind {
    INTEGER,
    REAL,
    VARIABLE,
    STRUCTURE,
    LIST,
    BLOCK,
    PARENTHESIZED_EXPRESSION,
    PREFIX_OPERATOR_EXPRESSION,
    INFIX_OPERATOR_EXPRESSION,
    POSTFIX_OPERATOR_EXPRESSION,
    CLAUSE,
    THEORY,
}
