package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `sub_atom/5` built-in (`sub_atom(Atom, Before, Length, After, SubAtom)`). */
fun subAtomContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("sub-atom/sub-atom-sub-is-var") {
        sub_atom("abracadabra", intOf(0), intOf(1), intOf(10), "S") yields {
            kotlin.collections.listOf(it.yes("S" to atomOf("a")))
        }
    }

    case("sub-atom/sub-atom-sub-is-var2") {
        sub_atom("abracadabra", intOf(6), intOf(5), intOf(0), "S") yields {
            kotlin.collections.listOf(it.yes("S" to atomOf("dabra")))
        }
    }

    case("sub-atom/sub-atom-sub-is-var3") {
        sub_atom("abracadabra", intOf(3), "L", intOf(3), "S") yields {
            kotlin.collections.listOf(it.yes("S" to atomOf("acada")), it.yes("L" to 5))
        }
    }

    case("sub-atom/sub-atom-double-var4") {
        sub_atom("banana", intOf(3), intOf(2), "T", "S") yields {
            kotlin.collections.listOf(it.yes("S" to atomOf("an")), it.yes("T" to 1))
        }
    }

    case("sub-atom/sub-atom-instantiation-error") {
        sub_atom("Banana", intOf(3), intOf(2), "Y", "S") yields {
            kotlin.collections.listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("sub_atom", 5),
                        varOf("Banana"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("sub-atom/sub-atom-type-error-atom-is-integer") {
        sub_atom(5, 2, 2, "_", "S") yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("sub_atom", 5),
                        TypeError.Expected.ATOM,
                        Integer.of("5"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    // sub_atom('Banana', 4, 2, _, 2).

    case("sub-atom/sub-atom-type-error-sub-is-integer") {
        sub_atom("banana", 4, 2, "_", 2) yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("sub_atom", 5),
                        TypeError.Expected.ATOM,
                        Integer.of("2"),
                        index = 4,
                    ),
                ),
            )
        }
    }

    // [sub_atom('Banana', a, 2, _, S2), type_error(integer,a)].

    case("sub-atom/sub-atom-type-error-before-is-not-integer") {
        sub_atom("banana", "a", 2, "_", "S") yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("sub_atom", 5),
                        TypeError.Expected.INTEGER,
                        Atom.of("a"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("sub-atom/sub-atom-type-error-length-is-not-integer") {
        sub_atom("banana", 4, "n", "_", "S") yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("sub_atom", 5),
                        TypeError.Expected.INTEGER,
                        Atom.of("n"),
                        index = 2,
                    ),
                ),
            )
        }
    }

    case("sub-atom/sub-atom-type-error-after-is-not-integer") {
        sub_atom("banana", 4, 2, "m", "S") yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("sub_atom", 5),
                        TypeError.Expected.INTEGER,
                        Atom.of("m"),
                        index = 3,
                    ),
                ),
            )
        }
    }
}
