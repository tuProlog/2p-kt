package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

class TestNotProvableImpl(
    private val solverFactory: SolverFactory,
    override val errorSignature: Signature
) : TestNotProvable {
    override fun testNPTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.no()
                ),
                solutions
            )
        }
    }

    override fun testNPCut() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"("!")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.no()
                ),
                solutions
            )
        }
    }

    override fun testNPCutFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(("!" and fail))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.yes()
                ),
                solutions
            )
        }
    }

    override fun testOrNotCutFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ((("X" `=` 1) or ("X" `=` 2)) and "not"(("!" and fail)))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    kotlin.collections.listOf(
                        yes("X" to 1),
                        yes("X" to 2)
                    )
                },
                solutions
            )
        }
    }

    override fun testNPEquals() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"(4 `=` 5)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.yes()
                ),
                solutions
            )
        }
    }

    override fun testNPNum() {
        prolog {
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
                            numOf(3)

                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testNPX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "not"("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            varOf("X")
                        )
                    )
                ),
                solutions
            )
        }
    }
}
