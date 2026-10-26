package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for `\+`/1 (negation as failure). */
fun notProvableContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("not", 1),
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("not-provable/np-true") {
        "not"(true) yields {
            kotlin.collections.listOf(
                it.no(),
            )
        }
    }

    case("not-provable/np-cut") {
        "not"("!") yields {
            kotlin.collections.listOf(
                it.no(),
            )
        }
    }

    case("not-provable/np-cut-fail") {
        "not"(("!" and fail)) yields {
            kotlin.collections.listOf(
                it.yes(),
            )
        }
    }

    case("not-provable/or-not-cut-fail") {
        ((("X" eq 1) or ("X" eq 2)) and "not"(("!" and fail))) yields {
            with(it) {
                kotlin.collections.listOf(
                    yes("X" to 1),
                    yes("X" to 2),
                )
            }
        }
    }

    case("not-provable/np-equals") {
        "not"(4 eq 5) yields {
            kotlin.collections.listOf(
                it.yes(),
            )
        }
    }

    case("not-provable/np-num") {
        "not"(3) yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        TypeError.Expected.CALLABLE,
                        numOf(3),
                    ),
                ),
            )
        }
    }

    case("not-provable/npx") {
        "not"("X") yields {
            kotlin.collections.listOf(
                it.halt(
                    InstantiationError.forGoal(
                        DummyInstances.executionContext,
                        errorSignature,
                        varOf("X"),
                    ),
                ),
            )
        }
    }
}
