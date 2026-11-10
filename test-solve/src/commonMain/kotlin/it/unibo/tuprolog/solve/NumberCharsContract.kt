package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `number_chars/2` built-in. */
fun numberCharsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("number-chars/number-chars-list-is-var") {
        number_chars(33, "L") yields {
            listOf(it.yes("L" to logicListOf("3", "3")))
        }
    }

    case("number-chars/number-chars-ok") { number_chars(33, logicListOf("3", "3")) yields { listOf(it.yes()) } }

    case("number-chars/number-chars-num-is-var") {
        number_chars("X", logicListOf("3", "3")) yields {
            listOf(it.yes("X" to 33))
        }
    }

    case("number-chars/number-chars-num-negative-is-var") {
        number_chars("X", logicListOf("-", "2", "5")) yields {
            listOf(it.yes("X" to intOf(-25)))
        }
    }

    case("number-chars/number-chars-space") {
        number_chars("X", logicListOf("\n", "3")) yields {
            listOf(it.yes("X" to 3))
        }
    }

    case("number-chars/number-chars-decimal-number") {
        number_chars("X", logicListOf("4", ".", "2")) yields {
            listOf(it.yes("X" to 4.2))
        }
    }

    // Tests the query
    // ```prolog
    // ?- number_chars(X,['3','.','9']).
    // ```
    // succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
    // binds variable `X` to `3.9`.
    scenario("number-chars/number-chars-complete-case") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            // val query = number_chars("X", listOf("3", ".", "3", "E", "+", "0"))
            val query = number_chars(X, logicListOf("3", ".", "9"))
            val solutions = solver.solve(query, mediumBudget).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 3.9)),
                solutions,
            )
        }
    }

    case("number-chars/number-chars-instation-errror") {
        number_chars("X", "L") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("number_chars", 2),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("number-chars/number-chars-syntax-error") {
        number_chars("X", logicListOf("3", atomOf("_"))) yields {
            listOf(it.halt(SyntaxError.of(DummyInstances.executionContext, "Cannot parse `3_` as a number")))
        }
    }

    case("number-chars/number-chars-type-error-character") {
        number_chars("X", logicListOf("4", 2)) yields {
            listOf(
                it.halt(
                    TypeError.forArgumentList(
                        DummyInstances.executionContext,
                        Signature("number_chars", 2),
                        TypeError.Expected.CHARACTER,
                        intOf(2),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("number-chars/number-chars-char-code") {
        number_chars("X", logicListOf("0", "'", atomOf("A"))) yields { listOf(it.yes("X" to 65)) }
    }

    case("number-chars/number-chars-compares-values") {
        number_chars(3.3, logicListOf("3", ".", "3", atomOf("E"), "+", "0")) yields { listOf(it.yes()) }
    }
}
