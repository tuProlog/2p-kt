package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestFunctorImpl(
    private val solverFactory: SolverFactory,
) : TestFunctor {
    override fun testFunArity() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a", "b", "c"), "foo", 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFunArityWithSub() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a", "b", "c"), "X", "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "foo", "Y" to 3)),
                solutions,
            )
        }
    }

    override fun testFunArityZero() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "foo", 0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to "foo")),
                solutions,
            )
        }
    }

    override fun testFunMats() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("mats"("A", "B"), "A", "B")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("A" to "mats", "B" to 2)),
                solutions,
            )
        }
    }

    override fun testFunWrongArity() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a"), "foo", 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFunWrongName() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a"), "fo", 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFunXNameYArity() { // solver says exception_error
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor(1, "X", "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 1, "Y" to 0)),
                solutions,
            )
        }
    }

    override fun testFunDecNum() { // solver says exception_error
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", 1.1, 0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 1.1)),
                solutions,
            )
        }
    }

    override fun testFunConsOf() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor(consOf(`_`, `_`), ".", 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFunEmptyList() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor(emptyLogicList, emptyLogicList, 0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testFunXYWrongArity() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "Y", 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            varOf("Y"),
                            1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFunXNArity() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "foo", "N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            varOf("N"),
                            index = 2,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFunXAArity() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "foo", "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 2,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFunNumName() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("F", 1.5, 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.ATOM,
                            numOf(1.5),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFunFooName() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("F", "foo"("a"), 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.ATOMIC,
                            "foo"("a"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFunFlag() {
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

    override fun testFunNegativeArity() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("T", "foo", intOf(-1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            DomainError.Expected.NOT_LESS_THAN_ZERO,
                            intOf(-1),
                            index = 2,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
