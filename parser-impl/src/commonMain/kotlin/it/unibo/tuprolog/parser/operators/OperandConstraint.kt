package it.unibo.tuprolog.parser.operators

enum class OperandConstraint {
    STRICT,
    NON_STRICT,
    ;

    fun accepts(
        operandPriority: Int,
        operatorPriority: Int,
    ): Boolean =
        when (this) {
            STRICT -> operandPriority < operatorPriority
            NON_STRICT -> operandPriority <= operatorPriority
        }

    fun maximumOperandPriority(operatorPriority: Int): Int =
        when (this) {
            STRICT -> operatorPriority - 1
            NON_STRICT -> operatorPriority
        }
}
