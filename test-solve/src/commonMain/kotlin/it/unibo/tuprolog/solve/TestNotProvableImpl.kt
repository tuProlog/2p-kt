package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestNotProvableImpl(
    private val solverFactory: SolverFactory,
    override val errorSignature: Signature,
) : TestNotProvable {
    override fun testNPTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.no(),
                ),
                solutions,
            )
        }
    }

    override fun testNPCut() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"("!")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.no(),
                ),
                solutions,
            )
        }
    }

    override fun testNPCutFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(("!" and fail))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.yes(),
                ),
                solutions,
            )
        }
    }

    override fun testOrNotCutFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ((("X" eq 1) or ("X" eq 2)) and "not"(("!" and fail)))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    kotlin.collections.listOf(
                        yes("X" to 1),
                        yes("X" to 2),
                    )
                },
                solutions,
            )
        }
    }

    override fun testNPEquals() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(4 eq 5)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.yes(),
                ),
                solutions,
            )
        }
    }

    override fun testNPNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            numOf(3),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testNPX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            varOf("X"),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
