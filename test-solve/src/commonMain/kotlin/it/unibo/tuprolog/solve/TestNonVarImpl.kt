package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestNonVarImpl(
    private val solverFactory: SolverFactory,
) : TestNonVar {
    override fun testNonVarNumber() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar(33.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testNonVarFoo() { // foo
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar("foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testNonVarFooCl() { // Foo (CapsLock)
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar("Foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testNonVarFooAssignment() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("foo" eq "Foo") and nonvar("Foo")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("Foo" to "foo")),
                solutions,
            )
        }
    }

    override fun testNonVarAnyTerm() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar(`_`)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testNonVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = nonvar("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }
}
