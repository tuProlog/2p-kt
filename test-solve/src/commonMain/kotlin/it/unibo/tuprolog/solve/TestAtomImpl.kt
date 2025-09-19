package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestAtomImpl(
    private val solverFactory: SolverFactory,
) : TestAtom {
    override fun testAtomAtom() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomString() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("string")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomAofB() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom("Var")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomEmptyList() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(emptyLogicList)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(6)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomNumDec() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atom(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }
}
