package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestAtomImpl(private val solverFactory: SolverFactory) : TestAtom {

    override fun testAtomAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom("atom")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAtomString() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom("string")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAtomAofB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom("a"("b"))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testAtomVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom("Var")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testAtomEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom(emptyList)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAtomNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom(6)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testAtomNumDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atom(3.3)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }
}