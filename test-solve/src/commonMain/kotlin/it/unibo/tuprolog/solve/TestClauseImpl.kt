package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestClauseImpl(private val solverFactory: SolverFactory) : TestClause {
    override fun testClauseXBody() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = clause("x", "Body")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testClauseAnyB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = clause(`_`, "B")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("clause", 2),
                            `_`,
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testClauseNumB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = clause(4, "B")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("clause", 2),
                            TypeError.Expected.CALLABLE,
                            numOf(4),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testClauseFAnyNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = clause("f"(`_`), 5)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("clause", 2),
                            TypeError.Expected.CALLABLE,
                            numOf(5),
                            index = 1
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testClauseAtomBody() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = clause(atom(`_`), "Body")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            Signature("clause", 2),
                            PermissionError.Operation.ACCESS,
                            PermissionError.Permission.PRIVATE_PROCEDURE,
                            "atom" / 1
                        )
                    )
                ),
                solutions
            )
        }
    }
}
