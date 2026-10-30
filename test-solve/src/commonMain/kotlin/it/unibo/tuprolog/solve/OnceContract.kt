package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `once/1` built-in. */
fun onceContract(
    solverFactory: SolverFactory,
    errorSignature: Signature = Signature("once", 1),
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("once/once-cut") { "once"("!") yields { kotlin.collections.listOf(it.yes()) } }

    case("once/once-cut-or") {
        ("once"("!") and (("X" eq 1) or ("X" eq 2))) yields {
            with(it) {
                kotlin.collections.listOf(
                    yes("X" to 1),
                    yes("X" to 2),
                )
            }
        }
    }

    case("once/once-repeat") { "once"(repeat) yields { kotlin.collections.listOf(it.yes()) } }
    case("once/once-fail") { "once"(fail) yields { kotlin.collections.listOf(it.no()) } }

    case("once/once-num") {
        "once"(3) yields {
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

    case("once/once-x") {
        "once"("X") yields {
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
