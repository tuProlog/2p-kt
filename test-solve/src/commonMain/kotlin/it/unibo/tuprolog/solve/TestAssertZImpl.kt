package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestAssertZImpl(
    private val solverFactory: SolverFactory,
) : TestAssertZ {
    override fun testAssertZClause() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = assertz("foo"("X") `if` ("X" then call("X")))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAssertZAny() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = assertz(`_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("assertz", 1),
                            `_`,
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testAssertZNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = assertz(4)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("assertz", 1),
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

    override fun testAssertZFooNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = assertz(("foo" `if` 4))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("assertz", 1),
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

    override fun testAssertZAtomTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = assertz(atom(`_`) `if` true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            Signature("assertz", 1),
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
