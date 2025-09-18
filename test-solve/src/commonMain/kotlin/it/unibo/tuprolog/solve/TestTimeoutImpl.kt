package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.LogicProgrammingScope
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.exception.TimeOutException
import it.unibo.tuprolog.solve.stdlib.primitive.FindAll
import it.unibo.tuprolog.solve.stdlib.primitive.Sleep

class TestTimeoutImpl(
    private val solverFactory: SolverFactory,
) : TestTimeout {
    override fun testSleep() {
        logicProgramming {
            val solver = solverFactory.solverWithDefaultBuiltins()
            solver.assertHasPredicateInAPI(Sleep)

            val query = "sleep"(mediumDuration)

            val solutions = solver.solveList(query, shortDuration)

            assertSolutionEquals(
                listOf<Solution>(
                    query.halt(
                        TimeOutException(context = DummyInstances.executionContext, exceededDuration = shortDuration),
                    ),
                ),
                solutions,
            )
        }
    }

    private fun testInfiniteCollectingGoal(goalProvider: LogicProgrammingScope.() -> Struct) {
        logicProgramming {
            val solver =
                solverFactory.solverWithDefaultBuiltins(
                    staticKb =
                        theoryOf(
                            fact { "nat"("z") },
                            rule { "nat"("s"(Z)) impliedBy "nat"(Z) },
                        ),
                )
            solver.assertHasPredicateInAPI(FindAll)

            val query = goalProvider()

            val solutions = solver.solveList(query, shortDuration)

            assertSolutionEquals(
                listOf<Solution>(
                    query.halt(
                        TimeOutException(context = DummyInstances.executionContext, exceededDuration = shortDuration),
                    ),
                ),
                solutions,
            )
        }
    }

    override fun testInfiniteFindAll() {
        testInfiniteCollectingGoal {
            "findall"(N, "nat"(N), L)
        }
    }

    override fun testInfiniteBagOf() {
        testInfiniteCollectingGoal {
            "bagof"(N, "nat"(N), L)
        }
    }

    override fun testInfiniteSetOf() {
        testInfiniteCollectingGoal {
            "setof"(N, "nat"(N), L)
        }
    }
}
