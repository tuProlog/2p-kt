package it.unibo.tuprolog.solve

import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.stdlib.primitive.FindAll
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep

class TestTimeoutImpl(private val solverFactory: SolverFactory) : TestTimeout {
    override fun testSleep() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            solver.assertHasPredicateInAPI(Sleep)

            val query = "sleep"(mediumDuration)

            val solutions = solver.solveList(query, shortDuration)

            assertSolutionEquals(
                ktListOf<Solution>(
                    query.halt(
                        TimeOutException(context = DummyInstances.executionContext, exceededDuration = shortDuration)
                    )
                ),
                solutions
            )
        }
    }

    override fun testInfiniteFindAll() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins(
                staticKb = theoryOf(
                    fact { "nat"("z") },
                    rule { "nat"("s"(Z)) impliedBy "nat"(Z) }
                )
            )
            solver.assertHasPredicateInAPI(FindAll)

            val query = "findall"(N, "nat"(N), L)

            val solutions = solver.solveList(query, shortDuration)

            assertSolutionEquals(
                ktListOf<Solution>(
                    query.halt(
                        TimeOutException(context = DummyInstances.executionContext, exceededDuration = shortDuration)
                    )
                ),
                solutions
            )
        }
    }
}
