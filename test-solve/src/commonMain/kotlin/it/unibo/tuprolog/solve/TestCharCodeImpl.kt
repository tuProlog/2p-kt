package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.exception.error.InstantiationError

class TestCharCodeImpl(private val solverFactory: SolverFactory) : TestCharCode{

    override fun testCharCodeSecondIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = char_code("a", "X")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to 97)),
                solutions
            )
        }
    }

    override fun testCharCodeFirstIsVar() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = char_code("X", intOf(97))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.yes("X" to "a")),
                solutions
            )
        }
    }

    override fun testCharCodeTypeError() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = atom_length("X", 4)
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        InstantiationError.forArgument(
                            DummyInstances.executionContext,
                            Signature("atom_length", 2),
                            varOf("X"),
                            index = 0
                        )
                    )
                ),
                solutions
            )
        }
    }

    override fun testCharCodeFails() {
        prolog {
            val solver = solverFactory.solverWithDefaultBuiltins()
            val query = char_code("g", intOf(105))
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions
            )
        }
    }


}