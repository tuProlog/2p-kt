package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestAndImpl(private val solverFactory: SolverFactory) : TestAnd {

    override fun testTermIsFreeVariable() { // test if the variable X is free
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ","("="("X", 1), "var(X)")
            val query = "X" `=` 1 and `var`("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testWithSubstitution(){
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
//            val query = ","("var(X)", "="("X",1))
            val query = `var`("X") and ("X" `=` 1)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to 1)),
                solutions
            )
        }
    }

    override fun testFailIsCallable() { //goal
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ","("fail", "call(3)")
            val query = fail() and call(3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.no()),
                solutions
            )
        }
    }

    override fun testNoFooIsCallable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ","("var(X)", "call(X)")
            val query = "nofoo"("X") and call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(
                    query.no() // TODO should be existence_error, but it is not supported ATM
                ),
                solutions
            )
        }
    }

    override fun testTrueVarCallable() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

//            val query = ","("="("X",true), "call(X)")
            val query = "X" `=` true and call("X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                ktListOf(query.yes("X" to true)),
                solutions
            )
        }
    }

}