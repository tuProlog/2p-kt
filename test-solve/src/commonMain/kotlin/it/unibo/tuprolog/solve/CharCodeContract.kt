package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError

/** Conformance tests for the ISO `char_code/2` built-in. */
fun charCodeContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("char-code/char-code-second-is-var") {
        char_code("a", "X") yields {
            kotlin.collections.listOf(it.yes("X" to 97))
        }
    }

    case("char-code/char-code-first-is-var") {
        char_code("X", intOf(97)) yields {
            kotlin.collections.listOf(it.yes("X" to "a"))
        }
    }

    case("char-code/char-code-type-error") {
        atom_length("X", 4) yields {
            kotlin.collections.listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_length", 2),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("char-code/char-code-fails") { char_code("g", intOf(105)) yields { kotlin.collections.listOf(it.no()) } }
}
