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
            kotlin.collections.listOf(it.yes("S" to atomOf("acada"), "L" to 5))
        }
    }

    case("sub-atom/sub-atom-double-var4") {
        sub_atom("banana", intOf(3), intOf(2), "T", "S") yields {
            kotlin.collections.listOf(it.yes("S" to atomOf("an"), "T" to 1))
        }
    }

    case("sub-atom/sub-atom-after-is-var") {
        sub_atom("abracadabra", intOf(0), intOf(5), "A", "S") yields {
            kotlin.collections.listOf(it.yes("A" to 6, "S" to atomOf("abrac")))
        }
    }

    case("sub-atom/sub-atom-before-is-var") {
        sub_atom("abracadabra", "B", intOf(5), intOf(0), "S") yields {
            kotlin.collections.listOf(it.yes("B" to 6, "S" to atomOf("dabra")))
        }
    }

    case("sub-atom/sub-atom-every-occurrence") {
        sub_atom("abracadabra", "B", intOf(2), "A", "ab") yields {
            kotlin.collections.listOf(it.yes("B" to 0, "A" to 9), it.yes("B" to 7, "A" to 2))
        }
    }

    case("sub-atom/sub-atom-fixed-length") {
        sub_atom("charity", "B", intOf(3), "A", "S") yields {
            kotlin.collections.listOf(
                it.yes("B" to 0, "A" to 4, "S" to atomOf("cha")),
                it.yes("B" to 1, "A" to 3, "S" to atomOf("har")),
                it.yes("B" to 2, "A" to 2, "S" to atomOf("ari")),
                it.yes("B" to 3, "A" to 1, "S" to atomOf("rit")),
                it.yes("B" to 4, "A" to 0, "S" to atomOf("ity")),
            )
        }
    }

    case("sub-atom/sub-atom-all-unbound") {
        sub_atom("ab", "B", "L", "A", "S") yields {
            kotlin.collections.listOf(
                it.yes("B" to 0, "L" to 0, "A" to 2, "S" to atomOf("")),
                it.yes("B" to 0, "L" to 1, "A" to 1, "S" to atomOf("a")),
                it.yes("B" to 0, "L" to 2, "A" to 0, "S" to atomOf("ab")),
                it.yes("B" to 1, "L" to 0, "A" to 1, "S" to atomOf("")),
                it.yes("B" to 1, "L" to 1, "A" to 0, "S" to atomOf("b")),
                it.yes("B" to 2, "L" to 0, "A" to 0, "S" to atomOf("")),
            )
        }
    }

    case("sub-atom/sub-atom-out-of-range") {
        sub_atom("ab", intOf(1), intOf(5), "A", "S") yields { kotlin.collections.listOf(it.no()) }
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
