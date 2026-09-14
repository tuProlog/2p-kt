package it.unibo.tuprolog.ui.gui.presentation

/** The syntactic role of one token, as classified by a `PrologSyntaxAnalyzer` for semantic-highlighting. */
enum class SemanticCategory {
    COMMENT,
    OPERATOR,
    PARENTHESIS,
    BRACE,
    BRACKET,
    FUNCTOR,
    ATOM,
    VARIABLE,
    NUMBER,
    STRING,
    FULL_STOP,
    DIRECTIVE,
    ERROR,
}
