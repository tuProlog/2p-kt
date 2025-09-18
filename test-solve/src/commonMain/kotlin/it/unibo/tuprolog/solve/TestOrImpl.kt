package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestOrImpl(
    private val solverFactory: SolverFactory,
) : TestOr {
    override fun testTrueOrFalse() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = true or fail
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    listOf(
                        yes(),
                        no(),
                    )
                },
                solutions,
            )
        }
    }

    override fun testCutFalseOrTrue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"(("!" and fail()), true)
            val query = "!" and fail or true
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testCutCall() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"("!", call(3))
            val query = "!" or call(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testCutAssignedValue() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"(("X" eq 1 and "!"), "X" eq 2)
            val query = ("X" eq 1 and "!") or ("X" eq 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to 1)),
                solutions,
            )
        }
    }

    override fun testOrDoubleAssignment() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"("X" eq 1, "X" eq 2)
            val query = ("X" eq 1) or ("X" eq 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    listOf(
                        yes("X" to 1),
                        yes("X" to 2),
                    )
                },
                solutions,
            )
        }
    }
}
