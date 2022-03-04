package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentFunctor<T : WithAssertingEquals> : FromSequence<T>, SolverFactory {

    fun testFunArity() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("foo"("a", "b", "c"), "foo", 3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFunArityWithSub() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("foo"("a", "b", "c"), "X", "Y")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "foo", "Y" to 3))

            expected.assertingEquals(solutions)
        }
    }

    fun testFunArityZero() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("X", "foo", 0)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to "foo"))

            expected.assertingEquals(solutions)
        }
    }

    fun testFunMats() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("mats"("A", "B"), "A", "B")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("A" to "mats", "B" to 2))

            expected.assertingEquals(solutions)
        }
    }

    fun testFunWrongArity() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("foo"("a"), "foo", 2)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFunWrongName() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("foo"("a"), "fo", 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.no())

            expected.assertingEquals(solutions)
        }
    }

    fun testFunXNameYArity() { // solver says exception_error
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor(1, "X", "Y")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1, "Y" to 0))

            expected.assertingEquals(solutions)
        }
    }

    fun testFunDecNum() { // solver says exception_error
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("X", 1.1, 0)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes("X" to 1.1))

            expected.assertingEquals(solutions)
        }
    }

    fun testFunConsOf() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor(consOf(`_`, `_`), ".", 2)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFunEmptyList() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor(emptyList, emptyList, 0)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(query.yes())

            expected.assertingEquals(solutions)
        }
    }

    fun testFunXYWrongArity() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("X", "Y", 3)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        varOf("Y"),
                        1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFunXNArity() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("X", "foo", "N")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    InstantiationError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        varOf("N"),
                        index = 2
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFunXAArity() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("X", "foo", "a")
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.INTEGER,
                        atomOf("a"),
                        index = 2
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFunNumName() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("F", 1.5, 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.ATOM,
                        numOf(1.5),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFunFooName() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("F", "foo"("a"), 1)
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    TypeError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        TypeError.Expected.ATOMIC,
                        "foo"("a"),
                        index = 1
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFunFlag() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = current_flag("max_arity", A) and (
                (X `is` (A + 1)) and functor(T, "foo", X)
                )
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    RepresentationError.of(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        RepresentationError.Limit.MAX_ARITY
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }

    fun testFunNegativeArity() {
        prolog {
            val solver = solverWithDefaultBuiltins()

            val query = functor("T", "foo", intOf(-1))
            val solutions = fromSequence(solver.solve(query, mediumDuration))
            val expected = fromSequence(
                query.halt(
                    DomainError.forArgument(
                        DummyInstances.executionContext,
                        Signature("functor", 3),
                        DomainError.Expected.NOT_LESS_THAN_ZERO,
                        intOf(-1),
                        index = 2
                    )
                )
            )

            expected.assertingEquals(solutions)
        }
    }
}
