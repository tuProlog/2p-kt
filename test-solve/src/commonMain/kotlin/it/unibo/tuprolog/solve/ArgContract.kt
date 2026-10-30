package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `arg/3` built-in. */
fun argContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("arg/arg-from-foo") { arg(1, "foo"("a", "b"), "a") yields { listOf(it.yes()) } }
    case("arg/arg-from-foo-x") { arg(1, "foo"("a", "b"), "X") yields { listOf(it.yes("X" to "a")) } }
    case("arg/arg-from-foo2") { arg(1, "foo"("X", "b"), "a") yields { listOf(it.yes("X" to "a")) } }

    case("arg/arg-from-foo-in-f") {
        arg(2, "foo"("a", "f"("X", "b"), "c"), "f"("a", "Y")) yields {
            with(it) {
                listOf(
                    yes("X" to "a", "Y" to "b"),
                )
            }
        }
    }

    case("arg/arg-from-foo-y") { arg(1, "foo"("X", "b"), "Y") yields { listOf(it.yes("Y" to "X")) } }
    case("arg/arg-from-foo-in-second-term") { arg(1, "foo"("a", "b"), "b") yields { listOf(it.no()) } }
    case("arg/arg-from-foo-in-foo") { arg(1, "foo"("a", "b"), "foo") yields { listOf(it.no()) } }
    case("arg/arg-number-from-foo") { arg(3, "foo"(3, 4), "N") yields { listOf(it.no()) } }

    case("arg/arg-x-from-foo") {
        arg("X", "foo"("a", "b"), "a") yields {
            listOf(
                it.yes("X" to 1),
            )
        }
    }

    case("arg/arg-number-from-x") {
        arg(1, "X", "a") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("arg", 3),
                        varOf("X"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("arg/arg-from-atom") {
        arg(0, "atom", "A") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("arg", 3),
                        TypeError.Expected.COMPOUND,
                        atomOf("atom"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("arg/arg-from-number") {
        arg(0, 3, "A") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("arg", 3),
                        TypeError.Expected.COMPOUND,
                        numOf(3),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("arg/negative-arg-from-foo") {
        arg(intOf(-3), "foo"("a", "b"), "A") yields {
            listOf(
                it.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("arg", 3),
                        DomainError.Expected.NOT_LESS_THAN_ZERO,
                        numOf(-3),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("arg/arg-a-from-foo") {
        arg("a", "foo"("a", "b"), "X") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("arg", 3),
                        TypeError.Expected.INTEGER,
                        atomOf("a"),
                        index = 0,
                    ),
                ),
            )
        }
    }
}
