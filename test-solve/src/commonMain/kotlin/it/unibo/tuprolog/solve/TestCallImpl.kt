package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestCallImpl(
    private val solverFactory: SolverFactory,
    override val errorSignature: Signature
) : TestCall {
    override fun testCallCut() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call("!")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testCallFail() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testCallFailX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testCallFailCall() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and call(1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testCallWriteX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
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

    override fun testCallWriteCall() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and call(1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            numOf(1)
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testCallX() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
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

    override fun testCallOne() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            numOf(1)
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testCallFailOne() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            fail and 1 // solver returns 1
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testCallWriteOne() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            write(3) and 1 // solver returns 1
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testCallTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(1 or true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            (1 or true) // solver returns 1
                        )
                    )
                ),
                solutions
            )
        }
    }
}
