package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

class TestAtomConcatImpl(private val solverFactory: SolverFactory) : TestAtomConcat {

    override fun testAtomConcatThirdIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("test", "concat", "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("testconcat"))),
                solutions
            )
        }
    }

    override fun testAtomConcatFails() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("test", "concat", "test")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }

    override fun testAtomConcatSecondIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("test", "X", "testTest")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("Test"))),
                solutions
            )
        }
    }

    override fun testAtomConcatFirstIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("X", "query", "testquery")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("test"))),
                solutions
            )
        }
    }
}
