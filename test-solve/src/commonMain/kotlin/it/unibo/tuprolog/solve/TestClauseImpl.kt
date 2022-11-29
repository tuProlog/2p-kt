package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestClauseImpl(private val solverFactory: SolverFactory) : TestClause {
    override fun testClauseXBody() {
        logicProgramming {
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
        logicProgramming {
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
        logicProgramming {
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
        logicProgramming {
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
        logicProgramming {
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

    override fun testClauseVariables() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theoryOf(
                    rule { "f"(X) impliedBy "g"(X) }
                )
            )

            var query = clause("f"(A), B)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(B to "g"(A))
                ),
                solutions
            )

            query = clause("f"(1), Z)
            solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.yes(Z to "g"(1))
                ),
                solutions
            )
        }
    }
}
