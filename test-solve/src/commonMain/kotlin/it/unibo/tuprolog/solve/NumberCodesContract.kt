package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `number_codes/2` built-in. */
fun numberCodesContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("number-codes/number-codes-list-is-var") {
        number_codes(33, "L") yields {
            listOf(it.yes("L" to logicListOf(51, 51)))
        }
    }

    case("number-codes/number-codes-num-is-decimal") {
        number_codes(33.1, "L") yields {
            listOf(it.yes("L" to logicListOf(51, 51, 46, 49)))
        }
    }

    case("number-codes/number-codes-list-is-var2") {
        number_codes(9921.1, "L") yields {
            listOf(it.yes("L" to logicListOf(57, 57, 50, 49, 46, 49)))
        }
    }

    case("number-codes/number-codes-ok") { number_codes(33, logicListOf(51, 51)) yields { listOf(it.yes()) } }

    case("number-codes/number-codes-complete-test") {
        number_codes(34, logicListOf(51, 52)) yields {
            listOf(it.yes())
        }
    }

    case("number-codes/number-codes-negative-number") {
        number_codes("X", logicListOf(45, 51, 46, 56)) yields {
            listOf(it.yes("X" to numOf("-3.8")))
        }
    }

    case("number-codes/number-codes-char") {
        number_codes("a", "L") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("number_codes", 2),
                        TypeError.Expected.NUMBER,
                        atomOf("a"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("number-codes/number-codes-syntax-error") {
        number_codes("X", logicListOf(51, 120)) yields {
            listOf(it.halt(SyntaxError.of(DummyInstances.executionContext, "Cannot parse `3x` as a number")))
        }
    }

    case("number-codes/number-codes-leading-layout") {
        number_codes("X", logicListOf(32, 51)) yields { listOf(it.yes("X" to 3)) }
    }

    case("number-codes/number-codes-char-code") {
        number_codes("X", logicListOf(48, 39, 97)) yields { listOf(it.yes("X" to 97)) }
    }

    case("number-codes/number-codes-compares-values") {
        // not 33.0: on Kotlin/JS, a whole Double literal becomes an integer term
        number_codes(3.3, logicListOf(48, 46, 51, 51, 69, 43, 48, 49)) yields { listOf(it.yes()) }
    }
}
