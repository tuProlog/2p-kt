@file:Suppress("LongMethod") // a case table

package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes
import it.unibo.tuprolog.solve.yields

fun concurrentSubAtomContract() =
    concurrentContract(
        disabled =
            setOf(
                "sub-atom/sub-atom-type-error-length-is-not-integer", // does not terminate
            ),
    ) {
        case("sub-atom/sub-atom-sub-is-var") {
            sub_atom("abracadabra", intOf(0), intOf(1), intOf(10), "S") yields {
                listOf(it.yes("S" to atomOf("a")))
            }
        }

        case("sub-atom/sub-atom-sub-is-var2") {
            sub_atom("abracadabra", intOf(6), intOf(5), intOf(0), "S") yields {
                listOf(it.yes("S" to atomOf("dabra")))
            }
        }

        scenario("sub-atom/sub-atom-sub-is-var3") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("abracadabra", intOf(3), "L", intOf(3), "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes("S" to atomOf("acada")),
                            query.yes("L" to 5),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("sub-atom/sub-atom-double-var4") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("banana", intOf(3), intOf(2), "T", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        sequenceOf(
                            query.yes("S" to atomOf("an")),
                            query.yes("T" to 1),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("sub-atom/sub-atom-instantiation-error") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("Banana", intOf(3), intOf(2), "Y", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            InstantiationError.forArgument(
                                DummyInstances.executionContext,
                                Signature("sub_atom", 5),
                                varOf("Banana"),
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("sub-atom/sub-atom-type-error-atom-is-integer") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom(5, 2, 2, "_", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("sub_atom", 5),
                                TypeError.Expected.ATOM,
                                Integer.of("5"),
                                index = 0,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        // sub_atom('Banana', 4, 2, _, 2).

        scenario("sub-atom/sub-atom-type-error-sub-is-integer") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("banana", 4, 2, "_", 2)
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("sub_atom", 5),
                                TypeError.Expected.ATOM,
                                Integer.of("2"),
                                index = 4,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        // [sub_atom('Banana', a, 2, _, S2), type_error(integer,a)].

        scenario("sub-atom/sub-atom-type-error-before-is-not-integer") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("banana", "a", 2, "_", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("sub_atom", 5),
                                TypeError.Expected.INTEGER,
                                Atom.of("a"),
                                index = 1,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("sub-atom/sub-atom-type-error-length-is-not-integer") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("banana", 4, "n", "_", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("sub_atom", 5),
                                TypeError.Expected.INTEGER,
                                Atom.of("n"),
                                index = 2,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }

        scenario("sub-atom/sub-atom-type-error-after-is-not-integer") {
            logicProgramming {
                val solver = solverWithDefaultBuiltins()

                val query = sub_atom("banana", 4, 2, "m", "S")
                val solutions = fromSequence(solver.solve(query, mediumDuration))
                val expected =
                    fromSequence(
                        query.halt(
                            TypeError.forArgument(
                                DummyInstances.executionContext,
                                Signature("sub_atom", 5),
                                TypeError.Expected.INTEGER,
                                Atom.of("m"),
                                index = 3,
                            ),
                        ),
                    )

                expected.assertingEquals(solutions)
            }
        }
    }
