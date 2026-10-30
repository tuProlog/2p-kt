package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

/** Conformance tests for `;`/2 (disjunction, `or`). */
fun orContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("or/true-or-false") {
        true or fail yields {
            with(it) {
                listOf(
                    yes(),
                    no(),
                )
            }
        }
    }

    // Tests the query
    // ```prolog
    // ';'((!, fail), true).
    // ```
    // fails on a solver initialized with default built-ins and with and empty theory.
    scenario("or/cut-false-or-true") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            //            val query = ";"(("!" and fail()), true)
            val query = "!" and fail or true
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    // Tests the query
    // ```prolog
    // ';'(!, call(3)).
    // ```
    // succeeds on a solver initialized with default built-ins and with and empty theory,
    // producing 1 solution with no variable bindings.
    scenario("or/cut-call") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            //            val query = ";"("!", call(3))
            val query = "!" or call(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    // Tests the query
    // ```prolog
    // ';'((X=1, !), X=2).
    // ```
    // succeeds on a solver initialized with default built-ins and with and empty theory,
    // producing 1 solution which binds variable `X` to the value `1`.
    scenario("or/cut-assigned-value") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            //            val query = ";"(("X" eq 1 and "!"), "X" eq 2)
            val query = ("X" eq 1 and "!") or ("X" eq 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }

    // Tests the query
    // ```prolog
    // ';'(X=1, X=2).
    // ```
    // succeeds on a solver initialized with default built-ins and with and empty theory,
    // producing 2 solutions which bind variable `X` to the values `1` and `2`, respectively.
    scenario("or/or-double-assignment") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            //            val query = ";"("X" eq 1, "X" eq 2)
            val query = ("X" eq 1) or ("X" eq 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    listOf(
                        yes("X" to 1),
                        yes("X" to 2),
                    )
                },
                solutions,
            )
        }
    }
}
