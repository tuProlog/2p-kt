package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `assertz/1` built-in. */
fun assertZContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("assert-z/assert-z-clause") { assertz("foo"("X") `if` ("X" then call("X"))) yields { listOf(it.yes()) } }

    case("assert-z/assert-z-any") {
        assertz(`_`) yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("assertz", 1),
                        `_`,
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("assert-z/assert-z-number") {
        assertz(4) yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("assertz", 1),
                        TypeError.Expected.CALLABLE,
                        numOf(4),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("assert-z/assert-z-foo-number") {
        assertz(("foo" `if` 4)) yields {
            listOf(
                it.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("assertz", 1),
                        DomainError.Expected.CLAUSE,
                        ("foo" `if` 4),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("assert-z/assert-z-atom-true") {
        assertz(atom(`_`) `if` true) yields {
            listOf(
                it.halt(
                    PermissionError.of(
                        DummyInstances.executionContext,
                        Signature("assertz", 1),
                        PermissionError.Operation.MODIFY,
                        PermissionError.Permission.PRIVATE_PROCEDURE,
                        "atom" / 1,
                    ),
                ),
            )
        }
    }
}
