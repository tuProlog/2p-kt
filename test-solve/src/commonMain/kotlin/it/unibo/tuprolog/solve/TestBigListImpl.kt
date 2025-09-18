package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming

internal class TestBigListImpl(
    private val solverFactory: SolverFactory,
) : TestBigList {
    override val shortDuration: TimeDuration
        get() = 6000

    override fun testBigListGeneration() {
        logicProgramming {
            val theory =
                theoryOf(
                    fact { "biglist"(0, logicListOf(0)) },
                    rule {
                        "biglist"(N, consOf(N, X)).impliedBy(
                            N greaterThan 0,
                            M `is` (N - 1),
                            "biglist"(M, X),
                        )
                    },
                )

            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory)

            val query = "biglist"(BigListOptions.SIZE, L)
            val solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(
                        L to logicListOf((0..BigListOptions.SIZE).reversed().map { Integer.of(it) }),
                    ),
                    query.no(),
                ),
                solutions,
            )
        }
    }
}
