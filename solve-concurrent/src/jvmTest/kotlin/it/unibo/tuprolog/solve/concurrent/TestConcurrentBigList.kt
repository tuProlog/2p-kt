package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TimeDuration
import it.unibo.tuprolog.solve.no
import it.unibo.tuprolog.solve.yes

interface TestConcurrentBigList<T : WithAssertingEquals> :
    FromSequence<T>,
    SolverFactory {
    override val shortDuration: TimeDuration
        get() = 4000

    fun testBigListGeneration() {
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

            val solver = solverWithDefaultBuiltins(staticKb = theory)

            val query = "biglist"(BigListOptions.SIZE, L)
            val solutions = fromSequence(solver.solve(query, longDuration))
            val expected =
                fromSequence(
                    sequenceOf(
                        query.yes(
                            L to logicListOf((0..BigListOptions.SIZE).reversed().map { Integer.of(it) }),
                        ),
                        query.no(),
                    ),
                )

            expected.assertingEquals(solutions)
        }
    }
}
