package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import kotlin.collections.listOf as ktListOf

internal class TestNonVarImpl(private val solverFactory: SolverFactory) : TestNonVar {

    override fun testNonVarNumber() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar(33.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testNonVarFoo() { //foo
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar("foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }

    override fun testNonVarFooCl() { //Foo (CapsLock)
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar("Foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testNonVarFooAssignment() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("foo" `=` "Foo") and nonvar("Foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes("Foo" to "foo")),
                    solutions
            )
        }
    }

    override fun testNonVarAnyTerm() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar(`_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.no()),
                    solutions
            )
        }
    }

    override fun testNonVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                    ktListOf(query.yes()),
                    solutions
            )
        }
    }
}
