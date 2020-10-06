package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog

class TestSubAtomImpl(private val solverFactory: SolverFactory) : TestSubAtom {
    override fun testSubAtomSubIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("abracadabra", intOf(0), intOf(5), "_", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("S" to atomOf("abrac"))),
                solutions
            )
        }
    }

    override fun testSubAtomSubIsVar2() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("abracadabra", "_", intOf(5), intOf(0), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to atomOf("dabra"))),
                solutions
            )
        }
    }

    override fun testSubAtomSubIsVar3() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("abracadabra", intOf(3), "L", intOf(3), "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("L" to 5).also { query.yes("X" to atomOf("acada")) }),
                solutions
            )
        }
    }

    override fun testSubAtomDoubleVar4() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = sub_atom("banana", intOf(3), intOf(2), "_", "S")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("S" to atomOf("an"))),
                solutions
            )
        }
    }
}
