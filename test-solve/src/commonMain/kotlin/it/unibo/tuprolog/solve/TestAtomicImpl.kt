package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestAtomicImpl(private val solverFactory: SolverFactory) : TestAtomic {

    override fun testAtomicAtom() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atomic("atom")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAtomicAofB() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atomic("a"("b"))
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testAtomicVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atomic("Var")
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testAtomicEmptyList() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atomic(emptyList)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAtomicNum() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atomic(6)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testAtomicNumDec() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            var query = atomic(3.3)
            var solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }
}