package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestFunctorImpl(private val solverFactory: SolverFactory) : TestFunctor{
    override fun testFunArity() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a", "b", "c"), "foo", 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFunArityWithSub() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a", "b", "c"), "X", "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to "foo", "Y" to 3)),
                solutions
            )
        }
    }

    override fun testFunArityZero() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "foo", 0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to "foo")),
                solutions
            )
        }
    }

    override fun testFunMats() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("mats"("A", "B"), "A", "B")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("A" to "mats", "B" to 2)),
                solutions
            )
        }
    }

    override fun testFunWrongArity() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a"), "foo", 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFunWrongName() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("foo"("a"), "fo", 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFunXNameYArity() { //solver says exception_error
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor(1, "X", "Y")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1, "Y" to 0)),
                solutions
            )
        }
    }

    override fun testFunDecNum() { //solver says exception_error
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", 1.1, 0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 1.1)),
                solutions
            )
        }
    }

    override fun testFunConsOf() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor(consOf(`_`,`_`), ".", 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFunEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor(emptyList, emptyList, 0)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions
            )
        }
    }

    override fun testFunXYWrongArity() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "Y", 3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forGoal(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            varOf("X")
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testFunXNArity() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "foo", "N")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forGoal(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            varOf("N")
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testFunXAArity() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("X", "foo", "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.INTEGER,
                            atomOf("a")
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testFunNumName() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("F", 1.5, 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.ATOM,
                            numOf(1.5)
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testFunFooName() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("F", "foo"("a"), 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            TypeError.Expected.ATOM, // it has to ATOMIC
                            "foo"("a")
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testFunFlag() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("current_prolog_flag"("max_arity", "A") and ("X" `is` "A" + 1)
                    and functor("T", "foo", "X"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testFunNegativeArity() { //solver returns yes(T to foo)
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = functor("T", "foo", intOf(-1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        DomainError.forGoal(
                            DummyInstances.executionContext,
                            Signature("functor", 3),
                            DomainError.Expected.NOT_LESS_THAN_ZERO,
                            intOf(-1)
                        )
                    )
                ),
                solutions
            )
        }
    }
}