package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `asserta/1` built-in. */
fun assertAContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("assert-a/assert-a-clause") {
        asserta(("bar"("X") `if` "X")) and clause("bar"("X"), "B") yields {
            listOf(it.yes("B" to call("X")))
        }
    }

    case("assert-a/assert-a-any") {
        asserta(`_`) yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("asserta", 1),
                        `_`,
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("assert-a/assert-a-number") {
        asserta(4) yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("asserta", 1),
                        TypeError.Expected.CALLABLE,
                        numOf(4),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("assert-a/assert-a-foo-number") {
        asserta("foo" `if` 4) yields {
            listOf(
                it.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("asserta", 1),
                        DomainError.Expected.CLAUSE,
                        ("foo" `if` 4),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("assert-a/assert-a-atom-true") {
        asserta(atom(`_`) `if` true) yields {
            listOf(
                it.halt(
                    PermissionError.of(
                        DummyInstances.executionContext,
                        Signature("asserta", 1),
                        PermissionError.Operation.MODIFY,
                        PermissionError.Permission.PRIVATE_PROCEDURE,
                        "atom" / 1,
                    ),
                ),
            )
        }
    }
}
