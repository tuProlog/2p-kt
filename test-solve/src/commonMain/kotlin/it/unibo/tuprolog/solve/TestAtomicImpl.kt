package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestAtomicImpl(private val solverFactory: SolverFactory) : TestAtomic {

    override fun testAtomicAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomicAofB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomicVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic("Var")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomicEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic(emptyList)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomicNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic(6)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }

    override fun testAtomicNumDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes()),
                solutions
            )
        }
    }
}
