package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

class TestAtomConcatImpl(
    private val solverFactory: SolverFactory,
) : TestAtomConcat {
    override fun testAtomConcatThirdIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("test", "concat", "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("testconcat"))),
                solutions,
            )
        }
    }

    override fun testAtomConcatFails() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("test", "concat", "test")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomConcatFailsNotPrefix() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("concat", "X", "testconcat")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomConcatFailsNotSuffix() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("X", "test", "testconcat")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomConcatSecondIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("test", "X", "testTest")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("Test"))),
                solutions,
            )
        }
    }

    override fun testAtomConcatFirstIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_concat("X", "query", "testquery")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("test"))),
                solutions,
            )
        }
    }
}
