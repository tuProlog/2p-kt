package it.unibo.tuprolog.parser.operators

enum class OperatorSpecifier(
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
