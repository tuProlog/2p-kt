package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.PermissionError

internal class TestRetractImpl(
    private val solverFactory: SolverFactory,
) : TestRetract {
    override fun testRetractNumIfX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = retract(":-"(4, "X"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        DomainError.forArgument(
                            DummyInstances.executionContext,
                            Signature("retract", 1),
                            DomainError.Expected.CLAUSE,
                            ":-"(4, "X"),
                            index = 0,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testRetractAtomEmptyList() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = retract((atom(`_`) `if` ("X" id emptyLogicList)))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        PermissionError.of(
                            DummyInstances.executionContext,
                            Signature("retract", 1),
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
