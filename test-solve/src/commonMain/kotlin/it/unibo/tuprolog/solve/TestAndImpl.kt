package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown

internal class TestAndImpl(private val solverFactory: SolverFactory) : TestAnd {

    override fun testTermIsFreeVariable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" `=` 1 and `var`("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testWithSubstitution() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = `var`("X") and ("X" `=` 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testFailIsCallable() { // goal
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = fail and call(3)
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
                flags = FlagStore.of(Unknown to Unknown.ERROR)
            )

            val query = "nofoo"("X") and call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            Signature("nofoo", 1)
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testTrueVarCallable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = "X" `=` true and call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to true)),
                solutions
            )
        }
    }
}
