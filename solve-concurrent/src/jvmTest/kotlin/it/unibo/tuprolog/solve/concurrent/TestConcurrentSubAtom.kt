package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.yes

interface TestConcurrentSubAtom<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testSubAtomSubIsVar() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = sub_atom("abracadabra", intOf(0), intOf(1), intOf(10), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to atomOf("a")))

            expected.assertingEquals(solutions)
        }
    }

    fun testSubAtomSubIsVar2() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = sub_atom("abracadabra", intOf(6), intOf(5), intOf(0), "S")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("S" to atomOf("dabra")))

            expected.assertingEquals(solutions)
        }
    }

    fun testSubAtomSubIsVar3() {
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

    fun testSubAtomDoubleVar4() {
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

    fun testSubAtomInstantiationError() {
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

    fun testSubAtomTypeErrorAtomIsInteger() {
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
    fun testSubAtomTypeErrorSubIsInteger() {
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
    fun testSubAtomTypeErrorBeforeIsNotInteger() {
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

    fun testSubAtomTypeErrorLengthIsNotInteger() {
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

    fun testSubAtomTypeErrorAfterIsNotInteger() {
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
