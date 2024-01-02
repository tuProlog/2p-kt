package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

class TestAtomCodesImpl(private val solverFactory: SolverFactory) : TestAtomCodes {
    override fun testAtomCodesSecondIsVar1() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_codes("abc", "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to listOf(97, 98, 99))),
                solutions,
            )
        }
    }

    override fun testAtomCodesSecondIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_codes("test", "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to listOf(116, 101, 115, 116))),
                solutions,
            )
        }
    }

    override fun testAtomCodesFirstIsVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_codes("X", listOf(97, 98, 99))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to "abc")),
                solutions,
            )
        }
    }

    override fun testAtomCodesNoVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_codes("test", listOf(116, 101, 115, 116))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomCodesFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_codes("test", listOf(112, 101, 115, 116))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
