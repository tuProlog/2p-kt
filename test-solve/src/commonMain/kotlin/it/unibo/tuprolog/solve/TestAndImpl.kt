package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestAndImpl(private val solverFactory: SolverFactory) : TestAnd {

    override fun testTermIsFreeVariable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ","("="("X", 1), "var(X)")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testWithSubstitution(){
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                    staticKb = theoryOf(
                            fact { "var(X)" }
                    )
            )

            val query = ","("var(X)", "="("X",1))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes("X" to 1)),
                    solutions
            )
        }
    }

    override fun testFailIsCallable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ","("fail", "call(3)")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testNoFooIsCallable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                    staticKb = theoryOf(
                            fact { "var(X)" }
                    )
            )

            val query = ","("var(X)", "call(X)")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testTermIsCallable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                    staticKb = theoryOf(
                            fact { "call(X)" }
                    )
            )

            val query = ","("="("X",true), "call(X)")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes("X" to true)),
                    solutions
            )
        }
    }

}