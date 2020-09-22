package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestAtomImpl(private val solverFactory: SolverFactory) : TestAtom {

    override fun testAtomAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomString() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("string")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomAofB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("Var")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(emptyList)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(6)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomNumDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }
}
