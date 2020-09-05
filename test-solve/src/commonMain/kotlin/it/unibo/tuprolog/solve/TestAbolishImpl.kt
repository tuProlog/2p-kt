package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestAbolishImpl(private val solverFactory: SolverFactory) : TestAbolish {
    override fun testDoubleAbolish() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish("abolish"/1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(query.no()), //TODO permission_error
                    solutions
            )
        }
    }

    override fun testAbolishFoo() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish("foo"/"a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(
                            query.halt(
                                    TypeError.forGoal(
                                            DummyInstances.executionContext,
                                            Signature("abolish", 1),
                                            TypeError.Expected.INTEGER,
                                            varOf("a")

                                    )
                            )
                    ),
                    solutions
            )
        }
    }

    override fun testAbolishFooNeg() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish("foo"/(-1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(
                            query.halt(
                                    DomainError.forGoal(
                                            DummyInstances.executionContext,
                                            Signature("abolish", 1),
                                            DomainError.Expected.NOT_LESS_THAN_ZERO,
                                            intOf(-1)

                                    )
                            )
                    ),
                    solutions
            )
        }
    }

    override fun testAbolishFlag() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("current_prolog_flag"("max_arity", "A") and ("X" `is` "A" + 1) and abolish("foo"/"X"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(
                            query.no()), // TODO representation_error
                    solutions
            )
        }
    }

    override fun testAbolish() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish(5/2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    kotlin.collections.listOf(
                            query.halt(
                                    TypeError.forGoal(
                                            DummyInstances.executionContext,
                                            Signature("abolish", 1),
                                            TypeError.Expected.ATOM,
                                            intOf(5)

                                    )
                            )
                    ),
                    solutions
            )
        }
    }
}