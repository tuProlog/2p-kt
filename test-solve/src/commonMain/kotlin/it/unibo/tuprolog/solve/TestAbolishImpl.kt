package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestAbolishImpl(
    private val solverFactory: SolverFactory,
) : TestAbolish {
    override fun testDoubleAbolish() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish("abolish" / 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            Signature("abolish", 1),
                            PermissionError.Operation.MODIFY,
                            PermissionError.Permission.PRIVATE_PROCEDURE,
                            "abolish" / 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAbolishFoo() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish("foo" / "a")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("abolish", 1),
                            TypeError.Expected.INTEGER,
                            atomOf("a"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAbolishFooNeg() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish("foo" / intOf(-1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("abolish", 1),
                            DomainError.Expected.NOT_LESS_THAN_ZERO,
                            intOf(-1),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAbolishFlag() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = (current_flag("max_arity", A) and ((X `is` (A + 1)) and abolish("foo" / X)))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        RepresentationError.of(
                            DummyInstances.executionContext,
                            Signature("abolish", 1),
                            RepresentationError.Limit.MAX_ARITY,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAbolish() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = abolish(intOf(5) / 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("abolish", 1),
                            TypeError.Expected.ATOM,
                            intOf(5),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
