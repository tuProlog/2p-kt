@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentFunctorContract() =
    concurrentContract {
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
        case("functor/fun-x-name-y-arity") { functor(1, "X", "Y") yields { listOf(it.yes("X" to 1, "Y" to 0)) } }
        case("functor/fun-dec-num") { functor("X", 1.1, 0) yields { listOf(it.yes("X" to 1.1)) } }
        case("functor/fun-cons-of") { functor(consOf(`_`, `_`), ".", 2) yields { listOf(it.yes()) } }
        case("functor/fun-empty-list") { functor(emptyLogicList, emptyLogicList, 0) yields { listOf(it.yes()) } }

        scenario("functor/fun-xy-wrong-arity") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = functor("X", "Y", 3)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                varOf("Y"),
                                1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("functor/fun-xn-arity") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = functor("X", "foo", "N")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                varOf("N"),
                                index = 2,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("functor/fun-xa-arity") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = functor("X", "foo", "a")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                TypeError.Expected.INTEGER,
                                atomOf("a"),
                                index = 2,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("functor/fun-num-name") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = functor("F", 1.5, 1)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                TypeError.Expected.ATOM,
                                numOf(1.5),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("functor/fun-foo-name") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = functor("F", "foo"("a"), 1)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                TypeError.Expected.ATOMIC,
                                "foo"("a"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("functor/fun-flag") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query =
                    current_flag("max_arity", A) and (
                        (X `is` (A + 1)) and functor(T, "foo", X)
                    )
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            RepresentationError.of(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                RepresentationError.Limit.MAX_ARITY,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("functor/fun-negative-arity") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = functor("T", "foo", intOf(-1))
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            DomainError.forArgument(
                                DummyInstances.executionContext,
                                Signature("functor", 3),
                                DomainError.Expected.NOT_LESS_THAN_ZERO,
                                intOf(-1),
                                index = 2,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }
