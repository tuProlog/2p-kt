package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.PermissionError

/** Conformance tests for the ISO `retract/1` built-in's error cases. */
fun retractContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("retract/retract-num-if-x") {
        retract(":-"(4, "X")) yields {
            listOf(
                it.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("retract", 1),
                        DomainError.Expected.CLAUSE,
                        ":-"(4, "X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("retract/retract-atom-empty-list") {
        retract((atom(`_`) `if` ("X" id emptyLogicList))) yields {
            listOf(
                it.halt(
                    PermissionError.of(
                        DummyInstances.executionContext,
                        Signature("retract", 1),
                        PermissionError.Operation.MODIFY,
                        PermissionError.Permission.PRIVATE_PROCEDURE,
                        "atom" / 1,
                    ),
                ),
            )
        }
    }
}
