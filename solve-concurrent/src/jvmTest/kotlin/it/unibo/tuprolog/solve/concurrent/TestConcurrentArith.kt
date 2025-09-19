package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentArith<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    fun testArithDiff() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = 0 arithNeq 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = 1.0 arithNeq 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) arithNeq (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = N arithNeq 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=\\=", 2),
                            N,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = "floot"(1) arithNeq 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=\\=", 2),
                            TypeError.Expected.EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArithEq() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = 0 arithEq 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = 1.0 arithEq 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) arithEq (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = N arithEq 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=:=", 2),
                            N,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = "floot"(1) arithEq 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=:=", 2),
                            TypeError.Expected.EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = 0.333 arithEq (numOf(1) / 3)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testArithGreaterThan() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = 0 greaterThan 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = 1.0 greaterThan 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) greaterThan (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = X greaterThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">", 2),
                            X,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = (2 + "floot"(1)) greaterThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">", 2),
                            TypeError.Expected.EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArithGreaterThanEq() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = 0 greaterThanOrEqualsTo 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = 1.0 greaterThanOrEqualsTo 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) greaterThanOrEqualsTo (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = X greaterThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">=", 2),
                            X,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = (2 + "floot"(1)) greaterThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature(">=", 2),
                            TypeError.Expected.EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArithLessThan() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = 0 lowerThan 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = 1.0 lowerThan 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) lowerThan (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.no())

            expected.assertingEquals(solutions)

            query = X lowerThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("<", 2),
                            X,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = (2 + "floot"(1)) lowerThan 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("<", 2),
                            TypeError.Expected.EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }

    fun testArithLessThanEq() {
        logicProgramming {
            val solver = solverWithDefaultBuiltins()

            var query = 0 lowerThanOrEqualsTo 1
            var solutions = fromSequence(solver.solve(query, mediumDuration))
            var expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = 1.0 lowerThanOrEqualsTo 1
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = (numOf(3) * 2) lowerThanOrEqualsTo (numOf(7) - 1)
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)

            query = X lowerThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=<", 2),
                            X,
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)

            query = (2 + "floot"(1)) lowerThanOrEqualsTo 5
            solutions = fromSequence(solver.solve(query, mediumDuration))
            expected =
                fromSequence(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("=<", 2),
                            TypeError.Expected.EVALUABLE,
                            "floot"(1),
                            index = 0,
                        ),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
