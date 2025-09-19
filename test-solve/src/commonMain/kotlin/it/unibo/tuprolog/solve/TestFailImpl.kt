package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.flags.Unknown

internal class TestFailImpl(
    private val solverFactory: SolverFactory,
) : TestFail {
    override fun testFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = fail
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testUndefPred() { // streams solver: `No(query=undef_pred)` instead of undef_pred/0
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    flags = FlagStore.of(Unknown to Unknown.ERROR),
                )

            val query = atomOf("undef_pred")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.halt(
                        ExistenceError.forProcedure(
                            DummyInstances.executionContext,
                            Signature("undef_pred", 0),
                        ),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testSetFlagFail() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("set_prolog_flag"("unknown" and fail) and "undef_pred")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }

    override fun testSetFlagWarning() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()

            val query = ("set_prolog_flag"("unknown" and "warning") and "undef_pred")
            val solutions = solver.solve(query, mediumDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(query.no()),
                solutions,
            )
        }
    }
}
