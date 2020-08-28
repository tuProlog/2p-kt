package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestOrImpl(private val solverFactory: SolverFactory) : TestOr {

    override fun testTrueOrFalse() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = true or fail
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    ktListOf(
                        yes(),
                        no()
                    )
                },
                solutions
            )
        }
    }

    override fun testCutFalseOrTrue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"(("!" and fail()), true)
            val query = "!" and fail or true
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testCutCall() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"("!", call(3))
            val query = "!" or call(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testCutAssignedValue() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"(("X" `=` 1 and "!"), "X" `=` 2)
            val query = ("X" `=` 1 and "!") or ("X" `=` 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testOrDoubleAssignment() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ";"("X" `=` 1, "X" `=` 2)
            val query = ("X" `=` 1) or ("X" `=` 2)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                with(query) {
                    ktListOf(
                        yes("X" to 1),
                        yes("X" to 2)
                    )
                },
                solutions
            )
        }
    }
}