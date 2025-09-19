package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming

class TestSetOfImpl(
    private val solverFactory: SolverFactory,
) : TestSetOf {
    override fun testSetOfBasic() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 1) or ("X" eq 2), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("S" to logicListOf(1, 2))),
                solutions,
            )
        }
    }

    override fun testSetOfX() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 1) or ("X" eq 2), "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to logicListOf(1, 2))),
                solutions,
            )
        }
    }

    override fun testSetOfSorted() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 2) or ("X" eq 1), "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("X" to logicListOf(2, 1))),
                solutions,
            )
        }
    }

    override fun testSetOfDoubled() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = setof("X", ("X" eq 2) or ("X" eq 2), "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("L" to logicListOf(2))),
                solutions,
            )
        }
    }

    override fun testSetOfFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = bagof("X", fail, "L")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testSetOfAsFindAll() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = setof("X", "Y" sup ((("X" eq 1) or ("Y" eq 1)) or (("X" eq 2) or ("Y" eq 2))), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                listOf(query.yes("S" to logicListOf(1, 2))),
                solutions,
            )
        }
    }
}
