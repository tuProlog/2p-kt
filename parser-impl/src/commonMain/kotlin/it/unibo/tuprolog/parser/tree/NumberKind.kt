package it.unibo.tuprolog.parser.tree

/** Semantic family of a parsed numeric literal. */
enum class NumberKind {
    DECIMAL_INTEGER,
    HEX_INTEGER,
    OCTAL_INTEGER,
    BINARY_INTEGER,
    CHARACTER_CODE,
    REAL,
}
