package it.unibo.tuprolog.parser.operators

/**
 * One of the seven ISO Prolog operator specifiers.
 *
 * The enum name reproduces the Prolog spelling: `f` is the operator, while `x` and `y` describe
 * strict and non-strict operand-priority constraints. [fixity], [left], and [right] expose that
 * spelling as data suitable for precedence-climbing parsers.
 *
 * @property fixity position of the operator relative to its operands
 * @property left constraint on the left operand, or `null` for a prefix operator
 * @property right constraint on the right operand, or `null` for a postfix operator
 * @see OperandConstraint
 */
enum class Associativity(
    val fixity: Fixity,
    val left: OperandConstraint?,
    val right: OperandConstraint?,
) {
    FX(Fixity.PREFIX, null, OperandConstraint.STRICT),
    FY(Fixity.PREFIX, null, OperandConstraint.NON_STRICT),
    XF(Fixity.POSTFIX, OperandConstraint.STRICT, null),
    YF(Fixity.POSTFIX, OperandConstraint.NON_STRICT, null),
    XFX(Fixity.INFIX, OperandConstraint.STRICT, OperandConstraint.STRICT),
    XFY(Fixity.INFIX, OperandConstraint.STRICT, OperandConstraint.NON_STRICT),
    YFX(Fixity.INFIX, OperandConstraint.NON_STRICT, OperandConstraint.STRICT),
}
