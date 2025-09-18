package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestAssertAImpl(
    private val solverFactory: SolverFactory,
) : TestAssertA {
    override fun testAssertAClause() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = asserta(("bar"("X") `if` "X")) and clause("bar"("X"), "B")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("B" to call("X"))),
                solutions,
            )
        }
    }

    override fun testAssertAAny() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = asserta(`_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("asserta", 1),
                            `_`,
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAssertANumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = asserta(4)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("asserta", 1),
                            TypeError.Expected.CALLABLE,
                            numOf(4),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAssertAFooNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = asserta("foo" `if` 4)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("asserta", 1),
                            DomainError.Expected.CLAUSE,
                            ("foo" `if` 4),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAssertAAtomTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = asserta(atom(`_`) `if` true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            Signature("asserta", 1),
                            PermissionError.Operation.MODIFY,
                            PermissionError.Permission.PRIVATE_PROCEDURE,
                            "atom" / 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
