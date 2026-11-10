package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `atom_concat/3` built-in. */
fun atomConcatContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("atom-concat/atom-concat-third-is-var") {
        atom_concat("test", "concat", "X") yields {
            kotlin.collections.listOf(it.yes("X" to atomOf("testconcat")))
        }
    }

    case("atom-concat/atom-concat-fails") {
        atom_concat("test", "concat", "test") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("atom-concat/atom-concat-fails-not-prefix") {
        atom_concat("concat", "X", "testconcat") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("atom-concat/atom-concat-fails-not-suffix") {
        atom_concat("X", "test", "testconcat") yields {
            kotlin.collections.listOf(it.no())
        }
    }

    case("atom-concat/atom-concat-second-is-var") {
        atom_concat("test", "X", "testTest") yields {
            kotlin.collections.listOf(it.yes("X" to atomOf("Test")))
        }
    }

    case("atom-concat/atom-concat-first-is-var") {
        atom_concat("X", "query", "testquery") yields {
            kotlin.collections.listOf(it.yes("X" to atomOf("test")))
        }
    }

    case("atom-concat/atom-concat-enumerates-splits") {
        atom_concat("X", "Y", "abc") yields {
            kotlin.collections.listOf(
                it.yes("X" to atomOf(""), "Y" to atomOf("abc")),
                it.yes("X" to atomOf("a"), "Y" to atomOf("bc")),
                it.yes("X" to atomOf("ab"), "Y" to atomOf("c")),
                it.yes("X" to atomOf("abc"), "Y" to atomOf("")),
            )
        }
    }

    case("atom-concat/atom-concat-whole-is-not-atom") {
        atom_concat("X", "Y", structOf("f", atomOf("a"))) yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_concat", 3),
                        TypeError.Expected.ATOM,
                        structOf("f", atomOf("a")),
                        index = 2,
                    ),
                ),
            )
        }
    }

    case("atom-concat/atom-concat-instantiation-error") {
        atom_concat("X", "b", "Y") yields {
            kotlin.collections.listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("atom_concat", 3),
                        varOf("X"),
                        index = 0,
                    ),
                ),
            )
        }
    }
}
