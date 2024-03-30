package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestCallImpl(
    private val solverFactory: SolverFactory,
    override val errorSignature: Signature,
) : TestCall {
    override fun testCallCut() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call("!")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCallFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCallFailX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCallFailCall() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and call(1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCallWriteX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
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

    override fun testCallWriteCall() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and call(1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            numOf(1),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testCallX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
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

    override fun testCallOne() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            numOf(1),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testCallFailOne() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(fail and 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            // solver returns 1
                            fail and 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testCallWriteOne() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(write(3) and 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            // solver returns 1
                            write(3) and 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testCallTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = call(1 or true)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forGoal(
                            DummyInstances.executionContext,
                            errorSignature,
                            TypeError.Expected.CALLABLE,
                            // solver returns 1
                            (1 or true),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }
}
