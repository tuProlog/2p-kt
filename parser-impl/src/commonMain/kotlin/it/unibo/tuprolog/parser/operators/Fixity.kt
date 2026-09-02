package it.unibo.tuprolog.parser.operators

/** Position of an operator relative to its operand or operands. */
enum class Fixity {
    PREFIX,
    INFIX,
    POSTFIX,
}
