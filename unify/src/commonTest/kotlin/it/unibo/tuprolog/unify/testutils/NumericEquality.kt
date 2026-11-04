package it.unibo.tuprolog.unify.testutils

import it.unibo.tuprolog.core.Term

/** The term equality of [it.unibo.tuprolog.unify.Unificator.naive], for test unificators to reuse. */
internal object NumericEquality {
    fun byValue(
        first: Term,
        second: Term,
    ): Boolean =
        when {
            first.isInteger && second.isInteger ->
                first.castToInteger().value.compareTo(second.castToInteger().value) == 0
            first.isNumber && second.isNumber ->
                first.castToNumeric().decimalValue.compareTo(second.castToNumeric().decimalValue) == 0
            else -> first == second
        }
}
