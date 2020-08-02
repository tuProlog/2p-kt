package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.collections.listOf as ktListOf

internal class TestAndImpl(private val solverFactory: SolverFactory) : TestAnd {

    override fun testTermIsFreeVariable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ","("X=1", "var(X)")
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

    override fun testFailIsAGoal() {
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

    override fun testNoFooIsAGoal() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins(
                    staticKb = theoryOf(
                            fact { "nofoo(X)" }
                    )
            )

            val query = ","("nofoo(X)", "call(X)")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(
                            query.halt(
                                    TypeError.forArgument(
                                            DummyInstances.executionContext,
                                            Signature(",", 3),
                                            TypeError.Expected.CALLABLE,
                                            varOf("nofoo(X)"),
                                            2
                                    )
                            )
                    ),
                    solutions
            )
        }
    }

    override fun testTermIsAGoal() {
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