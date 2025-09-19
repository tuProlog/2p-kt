package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentArg<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testArgFromFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromFooX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromFoo2() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "foo"("X", "b"), "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "a"))

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromFooInF() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(2, "foo"("a", "f"("X", "b"), "c"), "f"("a", "Y"))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "a", "Y" to "b"))

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromFooY() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "foo"("X", "b"), "Y")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("Y" to "X"))

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromFooInSecondTerm() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "b")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromFooInFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "foo"("a", "b"), "foo")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testArgNumberFromFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(3, "foo"(3, 4), "N")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testArgXFromFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg("X", "foo"("a", "b"), "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1))

            expected.assertingEquals(solutions)
        }
    }

    fun testArgNumberFromX() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(1, "X", "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            varOf("X"),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromAtom() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(0, "atom", "A")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            TypeError.Expected.COMPOUND,
                            atomOf("atom"),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArgFromNumber() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(0, 3, "A")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            TypeError.Expected.COMPOUND,
                            numOf(3),
                            index = 1,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testNegativeArgFromFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg(intOf(-3), "foo"("a", "b"), "A")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            DomainError.Expected.NOT_LESS_THAN_ZERO,
                            numOf(-3),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArgAFromFoo() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            val query = arg("a", "foo"("a", "b"), "X")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("arg", 3),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
