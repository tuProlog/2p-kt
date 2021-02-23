package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.prolog

internal class TestBigListImpl(private val solverFactory: SolverFactory) : TestBigList {

    override val shortDuration: TimeDuration
        get() = 2000

    companion object {
        private const val MAX = 1000
    }

    override fun testBigListGeneration() {
        prolog {
            val theory = theoryOf(
                fact { "biglist"(0, listOf(0)) },
                rule {
                    "biglist"(N, consOf(N, X)).impliedBy(
                        N greaterThan 0,
                        M `is` (N - 1),
                        "biglist"(M, X)
                    )
                },
            )

            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory)

            val query = "biglist"(MAX, L)
            val solutions = solver.solve(query, longDuration).toList()

            assertSolutionEquals(
                kotlin.collections.listOf(
                    query.yes(
                        L to listOf((0..MAX).reversed().map { Integer.of(it) })
                    ),
                    query.no()
                ),
                solutions
            )
        }
    }
}
