package it.unibo.tuprolog.solve.streams.primitive.integrationtest

import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheory
import it.unibo.tuprolog.solve.TestingClauseTheories.simpleFactTheoryNotableGoalToSolutions
import it.unibo.tuprolog.solve.assertSolutionEquals
import it.unibo.tuprolog.solve.changeQueryTo
import it.unibo.tuprolog.solve.hasSolutions
import it.unibo.tuprolog.solve.streams.primitive.testutils.PrimitiveUtils.assertOnlyOneSolution
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Conjunction
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Cut
import it.unibo.tuprolog.solve.streams.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes
import kotlin.test.Test

/**
 * Test class for the [Cut] and [Conjunction] integration
 *
 * @author Enrico
 */
internal class CutAndConjunctionIntegrationTest {

    @Test
    fun cutAsFirstGoalInConjunctionDoesNothing() {
        prolog {
            val modifiedSimpleFactDatabaseGoals =
                simpleFactTheoryNotableGoalToSolutions.map { (goal, expectedSolutions) ->
                    ("!" and goal).run { to(expectedSolutions.map { it.changeQueryTo(this) }) }
                }

            modifiedSimpleFactDatabaseGoals.forEach { (goal, solutionList) ->
                val request = createSolveRequest(
                    goal,
                    simpleFactTheory,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                )
                val solutions = Conjunction.wrappedImplementation(request).map { it.solution }.asIterable()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    @Test
    fun cutAsSecondGoalInConjunctionCutsFirstGoalAlternatives() {
        prolog {
            val modifiedSimpleFactDatabaseGoals =
                simpleFactTheoryNotableGoalToSolutions.map { (goal, expectedSolutions) ->
                    (goal and "!").hasSolutions({ expectedSolutions.first().changeQueryTo(this) })
                }

            modifiedSimpleFactDatabaseGoals.forEach { (goal, solutionList) ->
                val request = createSolveRequest(
                    goal,
                    simpleFactTheory,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
                )
                val solutions = Conjunction.wrappedImplementation(request).map { it.solution }.asIterable()

                assertSolutionEquals(solutionList, solutions)
            }
        }
    }

    @Test
    fun cutAsThirdGoalInConjunctionCutsOtherGoalsAlternatives() {
        prolog {
            val query = "g"("A") and "g"("B") and "!"
            val request =
                createSolveRequest(query, simpleFactTheory, mapOf(Conjunction.descriptionPair, Cut.descriptionPair))
            val responses = Conjunction.wrappedImplementation(request)

            assertOnlyOneSolution(query.yes("A" to "a", "B" to "a"), responses)
        }
    }

    @Test
    fun cutInMiddleOfGoalConjunctionWorksAsExpected() {
        prolog {
            val query = "g"("A") and "!" and "g"("B")
            val request =
                createSolveRequest(query, simpleFactTheory, mapOf(Conjunction.descriptionPair, Cut.descriptionPair))
            val responses = Conjunction.wrappedImplementation(request).map { it.solution }.asIterable()

            assertSolutionEquals(
                ktListOf(
                    query.yes("A" to "a", "B" to "a"),
                    query.yes("A" to "a", "B" to "b")
                ),
                responses
            )
        }
    }

    @Test
    fun multipleCutGoalInConjunctionWorksAsExpected() {
        prolog {
            val query = "g"("A") and "!" and "g"("B") and "!"
            val request =
                createSolveRequest(query, simpleFactTheory, mapOf(Conjunction.descriptionPair, Cut.descriptionPair))
            val responses = Conjunction.wrappedImplementation(request)

            assertOnlyOneSolution(query.yes("A" to "a", "B" to "a"), responses)
        }
    }

    @Test
    fun deepCutsInConjunctionsDoesntCutOuterScopeNodes() {
        prolog {
            val database = theoryOf(
                *simpleFactTheory.takeWhile { it.head != "g"("b") }.toTypedArray(),
                rule { "g"("cutting") `if` "g1"("deep1") },
                rule { "g1"("deep1") `if` "g2"("deep2") },
                rule { "g1"("deep1") `if` "g3"("deep3") },
                rule { "g2"("deep2") `if` "!" },
                rule { "g3"("deep3") `if` "!" },
                *simpleFactTheory.dropWhile { it.head != "g"("b") }.toTypedArray()
            )
            val query = "g"("A") and "!" and "g"("B")
            val request = createSolveRequest(query, database, mapOf(Conjunction.descriptionPair, Cut.descriptionPair))
            val responses = Conjunction.wrappedImplementation(request).map { it.solution }.asIterable()

            assertSolutionEquals(
                ktListOf(
                    query.yes("A" to "a", "B" to "a"),
                    query.yes("A" to "a", "B" to "cutting"),
                    query.yes("A" to "a", "B" to "cutting"),
                    query.yes("A" to "a", "B" to "b")
                ),
                responses
            )
        }
    }
}
