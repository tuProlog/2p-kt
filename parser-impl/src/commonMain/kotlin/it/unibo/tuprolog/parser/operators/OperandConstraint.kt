package it.unibo.tuprolog.parser.operators

/**
 * Priority relation imposed on one operand by a Prolog operator specifier.
 *
 * In ISO Prolog notation, `x` means [STRICT] and `y` means [NON_STRICT]. Lower numeric priorities
 * bind more tightly. See [ISO/IEC 13211-1](https://www.iso.org/standard/21413.html).
 */
enum class OperandConstraint {
    STRICT,
    NON_STRICT,
    ;

    /** Returns whether [operandPriority] satisfies this constraint for [operatorPriority]. */
    fun accepts(
        operandPriority: Int,
        operatorPriority: Int,
    ): Boolean =
        when (this) {
            STRICT -> operandPriority < operatorPriority
            NON_STRICT -> operandPriority <= operatorPriority
        }

    /** Returns the largest operand priority accepted for [operatorPriority]. */
    fun maximumOperandPriority(operatorPriority: Int): Int =
        when (this) {
            STRICT -> operatorPriority - 1
            NON_STRICT -> operatorPriority
        }
}
