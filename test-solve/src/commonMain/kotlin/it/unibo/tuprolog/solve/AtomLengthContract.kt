package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `atom_length/2` built-in. */
fun atomLengthContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("atom-length/atom-length-no-var") { atom_length("test", 4) yields { kotlin.collections.listOf(it.yes()) } }

    case("atom-length/atom-length-second-is-var") {
        atom_length("test", X) yields {
            kotlin.collections.listOf(it.yes("X" to 4))
        }
    }

    case("atom-length/atom-length-first-is-var") {
        char_code("X", "a") yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("char_code", 2),
                        TypeError.Expected.INTEGER,
                        atomOf("a"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("atom-length/atom-length-second-is-var2") {
        atom_length("testLength", X) yields {
            kotlin.collections.listOf(it.yes("X" to 10))
        }
    }

    case("atom-length/atom-length-fail") { atom_length("test", 5) yields { kotlin.collections.listOf(it.no()) } }
}
