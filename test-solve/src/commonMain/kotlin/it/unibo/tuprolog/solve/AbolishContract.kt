package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the (deprecated ISO) `abolish/1` built-in. */
fun abolishContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("abolish/double-abolish") {
        abolish("abolish" / 1) yields {
            kotlin.collections.listOf(
                it.halt(
                    PermissionError.of(
                        DummyInstances.executionContext,
                        Signature("abolish", 1),
                        PermissionError.Operation.MODIFY,
                        PermissionError.Permission.PRIVATE_PROCEDURE,
                        "abolish" / 1,
                    ),
                ),
            )
        }
    }

    case("abolish/abolish-foo") {
        abolish("foo" / "a") yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("abolish", 1),
                        TypeError.Expected.INTEGER,
                        atomOf("a"),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("abolish/abolish-foo-neg") {
        abolish("foo" / intOf(-1)) yields {
            kotlin.collections.listOf(
                it.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("abolish", 1),
                        DomainError.Expected.NOT_LESS_THAN_ZERO,
                        intOf(-1),
                        index = 0,
                    ),
                ),
            )
        }
    }

    case("abolish/abolish-flag") {
        (current_flag("max_arity", A) and ((X `is` (A + 1)) and abolish("foo" / X))) yields {
            kotlin.collections.listOf(
                it.halt(
                    RepresentationError.of(
                        DummyInstances.executionContext,
                        Signature("abolish", 1),
                        RepresentationError.Limit.MAX_ARITY,
                    ),
                ),
            )
        }
    }

    case("abolish/abolish") {
        abolish(intOf(5) / 2) yields {
            kotlin.collections.listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("abolish", 1),
                        TypeError.Expected.ATOM,
                        intOf(5),
                        index = 0,
                    ),
                ),
            )
        }
    }
}
