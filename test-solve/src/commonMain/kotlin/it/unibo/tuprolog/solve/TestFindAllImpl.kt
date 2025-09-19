package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

internal class TestFindAllImpl(
    private val solverFactory: SolverFactory,
    override val errorSignature: Signature,
) : TestFindAll {
    override fun testFindXInDiffValues() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 1) or ("X" eq 2), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("S" to logicListOf(1, 2))),
                solutions,
            )
        }
    }

    override fun testFindSumResult() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("+"("X", "Y"), "X" eq 1, "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("S" to logicListOf(1 + "Y"))),
                solutions,
            )
        }
    }

    override fun testFindXinFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", fail, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("L" to emptyLogicList)),
                solutions,
            )
        }
    }

    override fun testFindXinSameXValues() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 1) or ("X" eq 1), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("S" to logicListOf(1, 1))),
                solutions,
            )
        }
    }

    override fun testResultListIsCorrect() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 2) or ("X" eq 1), logicListOf(1, 2))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testFindXtoDoubleAssigment() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", ("X" eq 1) or ("X" eq 2), logicListOf("X", "Y"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 1, "Y" to 2)),
                solutions,
            )
        }
    }

    override fun testFindXinGoal() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", "Goal", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("findall", 3),
                            varOf("Goal"),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFindXinNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", 4, "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.halt(
                        TypeError.forArgument(
                            DummyInstances.executionContext,
                            Signature("findall", 3),
                            TypeError.Expected.CALLABLE,
                            numOf(4),
                            index = 1,
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testFindXinCall() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = findall("X", call(1), "S")
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
}
