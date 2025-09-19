package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestAtomicImpl(
    private val solverFactory: SolverFactory,
) : TestAtomic {
    override fun testAtomicAtom() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic("atom")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomicAofB() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic("a"("b"))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomicVar() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic("Var")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testAtomicEmptyList() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic(emptyLogicList)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomicNum() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic(6)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }

    override fun testAtomicNumDec() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = atomic(3.3)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes()),
                solutions,
            )
        }
    }
}
