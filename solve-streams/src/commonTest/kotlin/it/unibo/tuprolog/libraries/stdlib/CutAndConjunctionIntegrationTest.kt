package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.solver.testutils.SolverTestUtils
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.collections.listOf as ktListOf

/**
 * Test class for the [Cut] and [Conjunction] integration
 *
 * @author Enrico
 */
internal class CutAndConjunctionIntegrationTest {

    @Test
    fun cutAsFirstGoalInConjunctionDoesNothing() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(tupleOf(atomOf("!"), structOf("g", varOf("A"))),
                    SolverTestUtils.factDatabase,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )
            val responses = Conjunction.primitive(request).toList()
            assertEquals(2, responses.count())
            assertEquals(
                    ktListOf<Atom>(atomOf("a"), atomOf("b")),
                    responses.map { it.solution.substitution.values.single() }.toList()
            )
        }
    }

    @Test
    fun cutAsSecondGoalInConjunctionCutsFirstGoalAlternatives() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(tupleOf(structOf("g", varOf("A")), atomOf("!")),
                    SolverTestUtils.factDatabase,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )

            val responses = Conjunction.primitive(request).toList()
            assertEquals(1, responses.count())
            assertEquals(atomOf("a"), responses.single().solution.substitution.values.single())
        }
    }

    @Test
    fun cutAsThirdGoalInConjunctionCutsOtherGoalsAlternatives() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(
                    tupleOf(ktListOf(
                            structOf("g", varOf("A")),
                            structOf("g", varOf("B")),
                            atomOf("!")
                    )),
                    SolverTestUtils.factDatabase,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )
            val responses = Conjunction.primitive(request).toList()
            assertEquals(1, responses.count())
            assertEquals(
                    ktListOf(atomOf("a"), atomOf("a")),
                    responses.single().solution.substitution.values.toList()
            )
        }
    }

    @Test
    fun cutInMiddleOfGoalConjunctionWorksAsExpected() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(
                    tupleOf(ktListOf(
                            structOf("g", varOf("A")),
                            atomOf("!"),
                            structOf("g", varOf("B"))
                    )),
                    SolverTestUtils.factDatabase,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )
            val responses = Conjunction.primitive(request).toList()
            assertEquals(2, responses.count())
            assertEquals(
                    ktListOf(
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("a")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("b"))
                    ),
                    responses.map { it.solution.substitution }.toList()
            )
        }
    }

    @Test
    fun multipleCutGoalInConjunctionWorksAsExpected() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(
                    tupleOf(ktListOf(
                            structOf("g", varOf("A")),
                            atomOf("!"),
                            structOf("g", varOf("B")),
                            atomOf("!")
                    )),
                    SolverTestUtils.factDatabase,
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )
            val responses = Conjunction.primitive(request).toList()
            assertEquals(1, responses.count())
            assertEquals(
                    ktListOf(atomOf("a"), atomOf("a")),
                    responses.single().solution.substitution.values.toList()
            )
        }
    }

    @Test
    fun nestedCutsInConjunctionsWorkAsExpected() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(
                    tupleOf(ktListOf(
                            structOf("g", varOf("A")),
                            atomOf("!"),
                            structOf("g", varOf("B"))
                    )),
                    ClauseDatabase.of(
                            SolverTestUtils.factDatabase.takeWhile { it.head != structOf("g", atomOf("b")) } +
                                    ktListOf(Rule.of(structOf("g", atomOf("cutting")), atomOf("!"))) +
                                    SolverTestUtils.factDatabase.dropWhile { it.head != structOf("g", atomOf("b")) }
                    ),
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )
            val responses = Conjunction.primitive(request).toList()
            assertEquals(2, responses.count())
            assertEquals(
                    ktListOf(
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("a")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("cutting"))
                    ),
                    responses.map { it.solution.substitution }.toList()
            )
        }
    }

    @Test
    fun deepCutsInConjunctionsDoesntCutOuterScopeNodes() {
        Scope.empty().where {
            val request = SolverTestUtils.createSolveRequest(
                    tupleOf(ktListOf(
                            structOf("g", varOf("A")),
                            atomOf("!"),
                            structOf("g", varOf("B"))
                    )),
                    ClauseDatabase.of(
                            SolverTestUtils.factDatabase.takeWhile { it.head != structOf("g", atomOf("b")) } +
                                    ktListOf(
                                            Rule.of(structOf("g", atomOf("cutting")), structOf("g1", atomOf("deep1"))),
                                            Rule.of(structOf("g1", atomOf("deep1")), structOf("g2", atomOf("deep2"))),
                                            Rule.of(structOf("g1", atomOf("deep1")), structOf("g3", atomOf("deep3"))),
                                            Rule.of(structOf("g2", atomOf("deep2")), atomOf("!")),
                                            Rule.of(structOf("g3", atomOf("deep3")), atomOf("!"))) +
                                    SolverTestUtils.factDatabase.dropWhile { it.head != structOf("g", atomOf("b")) }
                    ),
                    mapOf(Conjunction.descriptionPair, Cut.descriptionPair)
            )
            val responses = Conjunction.primitive(request).toList()
            assertEquals(4, responses.count())
            assertEquals(
                    ktListOf(
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("a")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("cutting")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("cutting")),
                            Substitution.of(varOf("A") to atomOf("a"), varOf("B") to atomOf("b"))
                    ),
                    responses.map { it.solution.substitution }.toList()
            )
        }
    }
}
