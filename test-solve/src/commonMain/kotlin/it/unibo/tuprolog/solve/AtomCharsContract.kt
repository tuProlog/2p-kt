package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `atom_chars/2` built-in. */
fun atomCharsContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("atom-chars/atom-chars-test-first-is-var") {
        atom_chars("X", logicListOf("t", "e", "s", "t")) yields {
            listOf(it.yes("X" to "test"))
        }
    }

    case("atom-chars/atom-chars-test-yes") {
        atom_chars("test", logicListOf("t", "e", "s", "t")) yields {
            listOf(it.yes())
        }
    }

    case("atom-chars/atom-chars-test-one-char-is-var") {
        atom_chars("test", logicListOf("t", "e", "s", "T")) yields {
            listOf(it.yes(("T" to "t")))
        }
    }

    case("atom-chars/atom-chars-test-failure") {
        atom_chars("test1", logicListOf("t", "e", "s", "T")) yields {
            listOf(it.no())
        }
    }

    case("atom-chars/atom-chars-test-empty") {
        atom_chars("", "L") yields {
            listOf(it.yes("L" to emptyLogicList))
        }
    }

    case("atom-chars/atom-chars-test-list-head") {
        atom_chars("ac", logicListOf("a", "C")) yields {
            listOf(it.yes("C" to "c"))
        }
    }

    case("atom-chars/atom-chars-test-istantation-error-check") {
        atom_chars("A", "L") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_chars", 2),
                        varOf("A"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("atom-chars/atom-chars-test-type-error-check") {
        atom_chars("A", "iso") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_chars", 2),
                        TypeError.Expected.LIST,
                        atomOf("iso"),
                        index = 1,
                    ),
                ),
            )
        }
    }
}
