package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `clause/2` built-in. */
fun clauseContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("clause/clause-x-body") { clause("x", "Body") yields { listOf(it.no()) } }

    case("clause/clause-any-b") {
        clause(`_`, "B") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("clause", 2),
                        `_`,
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("clause/clause-num-b") {
        clause(4, "B") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("clause", 2),
                        TypeError.Expected.CALLABLE,
                        numOf(4),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("clause/clause-f-any-num") {
        clause("f"(`_`), 5) yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("clause", 2),
                        TypeError.Expected.CALLABLE,
                        numOf(5),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("clause/clause-atom-body") {
        clause(atom(`_`), "Body") yields {
            listOf(
                it.halt(
                    PermissionError.of(
                        DummyInstances.executionContext,
                        Signature("clause", 2),
                        PermissionError.Operation.ACCESS,
                        PermissionError.Permission.PRIVATE_PROCEDURE,
                        "atom" / 1,
                    ),
                ),
            )
        }
    }

    // Tests the queries
    // ```prolog
    // f(X) :- g(X).
    // ?- clause(f(A),B).
    // ```
    // succeeds in such a way that `B=g(A)`, whereas
    // ```
    // ?- clause(f(1), Z).
    // ```
    // succeeds in such a way that `Z=g(1)`
    scenario("clause/clause-variables") {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theoryOf(
                            rule { "f"(X) impliedBy "g"(X) },
                        ),
                )

            var query = clause("f"(A), B)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(B to "g"(A)),
                ),
                solutions,
            )

            query = clause("f"(1), Z)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(Z to "g"(1)),
                ),
                solutions,
            )
        }
    }
}
