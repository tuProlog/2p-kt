package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/** Conformance tests for the ISO `functor/3` built-in. */
fun functorContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    case("functor/fun-arity") { functor("foo"("a", "b", "c"), "foo", 3) yields { listOf(it.yes()) } }

    case("functor/fun-arity-with-sub") {
        functor("foo"("a", "b", "c"), "X", "Y") yields {
            listOf(it.yes("X" to "foo", "Y" to 3))
        }
    }

    case("functor/fun-arity-zero") { functor("X", "foo", 0) yields { listOf(it.yes("X" to "foo")) } }

    case("functor/fun-mats") {
        functor("mats"("A", "B"), "A", "B") yields {
            listOf(it.yes("A" to "mats", "B" to 2))
        }
    }

    case("functor/fun-wrong-arity") { functor("foo"("a"), "foo", 2) yields { listOf(it.no()) } }
    case("functor/fun-wrong-name") { functor("foo"("a"), "fo", 1) yields { listOf(it.no()) } }
    // solver says exception_error
    case("functor/fun-x-name-y-arity") { functor(1, "X", "Y") yields { listOf(it.yes("X" to 1, "Y" to 0)) } }
    // solver says exception_error
    case("functor/fun-dec-num") { functor("X", 1.1, 0) yields { listOf(it.yes("X" to 1.1)) } }
    case("functor/fun-cons-of") { functor(consOf(`_`, `_`), ".", 2) yields { listOf(it.yes()) } }
    case("functor/fun-empty-list") { functor(emptyLogicList, emptyLogicList, 0) yields { listOf(it.yes()) } }

    case("functor/fun-xy-wrong-arity") {
        functor("X", "Y", 3) yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        varOf("Y"),
                        1,
                    ),
                ),
            )
        }
    }

    case("functor/fun-xn-arity") {
        functor("X", "foo", "N") yields {
            listOf(
                it.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        varOf("N"),
                        index = 2,
                    ),
                ),
            )
        }
    }

    case("functor/fun-xa-arity") {
        functor("X", "foo", "a") yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.INTEGER,
                        atomOf("a"),
                        index = 2,
                    ),
                ),
            )
        }
    }

    case("functor/fun-num-name") {
        functor("F", 1.5, 1) yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.ATOM,
                        numOf(1.5),
                        index = 1,
                    ),
                ),
            )
        }
    }

    case("functor/fun-foo-name") {
        functor("F", "foo"("a"), 1) yields {
            listOf(
                it.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.ATOMIC,
                        "foo"("a"),
                        index = 1,
                    ),
                ),
            )
        }
    }

    // Tests the query
    // ```prolog
    // ?- (current_prolog_flag(max_arity,A), X is A + 1, functor(T, foo, X)).
    // ```
    // fails on a solver initialized with default built-ins and with and empty theory,
    // producing exception `representation_error(max_arity)`.
    scenario("functor/fun-flag") {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query =
                current_flag("max_arity", A) and (
                    (X `is` (A + 1)) and functor(T, "foo", X)
                )
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        RepresentationError.of(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            RepresentationError.Limit.MAX_ARITY,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    case("functor/fun-negative-arity") {
        functor("T", "foo", intOf(-1)) yields {
            listOf(
                it.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        DomainError.Expected.NOT_LESS_THAN_ZERO,
                        intOf(-1),
                        index = 2,
                    ),
                ),
            )
        }
    }
}
