package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.collections.List as KtList


class PrototypeProperIndexingTest(
    private val clauseDatabaseGenerator: (Iterable<Clause>) -> ClauseDatabase
) {

//    private val genericClausesTheory =
//        listOf(
//            Fact.of(Truth.TRUE),
//            Fact.of(Truth.FAIL),
//            Fact.of(Atom.of("a")),
//            Fact.of(Atom.of("other")),
//            Fact.of(Struct.of("a", Atom.of("other"))),
//            Fact.of(Struct.of("other", Integer.of(1))),
//            Fact.of(Tuple.of(Var.of("A"), Var.of("B"))),
//            Rule.of(Atom.of("a"), Atom.of("other")),
//            Rule.of(Tuple.of(Var.of("A"), Var.of("B")), Atom.of("a"))
//        )

    private val desiredIndexingOverSameFunctorAndArity =
        listOf(
            Fact.of(Struct.of("f", Var.of("X"))),
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Var.of("Y")))
        )

    fun correctIndexingOverDedicatedClauseDatabase() {
        val generatedClausesIndexing = clauseDatabaseGenerator(desiredIndexingOverSameFunctorAndArity).clauses

        testVisualisation(desiredIndexingOverSameFunctorAndArity, generatedClausesIndexing)
        assertClausesHaveSameLengthAndContent(desiredIndexingOverSameFunctorAndArity, generatedClausesIndexing)
    }

    fun correctIndexingOnInitializedClauseDatabaseWithoutStructurallyEqualsClauses() {

    }

    fun correctIndexingOnInitializedClauseDatabaseWithStructurallyEqualsClauses() {

    }

    fun correctIndexingOnInitializedClauseDatabaseWithStructurallyEqualsClausesAndClausesRepetition() {

    }

    fun correctIndexingAfterClauseDatabasesConcatenation() {

    }

    fun correctIndexingAfterAssertionA() {

    }

    fun correctIndexingAfterAssertionZ() {

    }

    fun correctIndexingAfterSingleClauseRetracting() {

    }

    fun correctIndexingAfterMultipleClausesRetracting() {

    }

    private fun assertClausesHaveSameLengthAndContent(a: Iterable<Clause>, b: Iterable<Clause>) {
        val i = a.iterator()
        val j = b.iterator()

        while (i.hasNext() && j.hasNext()) {
            assertTermsAreEqual(i.next(), j.next())
        }

        assertEquals(i.hasNext(), j.hasNext())
    }

    private fun assertTermsAreEqual(expected: Term, actual: Term) {
        assertEquals(expected.isGround, actual.isGround)
        if (expected.isGround) {
            assertEquals(
                expected,
                actual,
                message = """Comparing:
                    |   actual: $actual
                    |     type: ${actual::class}
                    | expected: $expected
                    |     type: ${expected::class}
                """.trimMargin()
            )
        } else {
            when {
                expected is Var && actual is Var -> {
                    assertEquals(expected.name, actual.name)
                }
                expected is Constant && actual is Constant -> {
                    assertEquals(expected, actual)
                }
                expected is Struct && actual is Struct -> {
                    assertEquals(expected.functor, actual.functor)
                    assertEquals(expected.arity, actual.arity)
                    for (i in 0 until expected.arity) {
                        assertTermsAreEqual(expected[i], actual[i])
                    }
                }
            }
            assertEquals(
                expected.variables.toSet().size,
                actual.variables.toSet().size
            )
        }
    }

    private fun <T> testVisualisation(expected: T, actual:T) {
        println("Expected: $expected")
        println("Actual: $actual")
    }

    /*
    fun expectedIndexingReflectsActualClauseDatabaseIndexing() {
        val toBeTestedIndexing = initialFilledClauseDatabase.clauses
        val expectedIndexing = expectedIndexingOfFreshGeneratedClauseDatabase.second()

        assertEquals(expectedIndexing, toBeTestedIndexing)
        assertTrue(indexingInvariantsHolds(expectedIndexing.toList(), toBeTestedIndexing.toList()))
    }

    fun addingTwoClauseDatabaseMaintainsIndexingRationale() {

    }

    fun assertingSingleClauseToClauseDatabaseHeadMaintainsIndexingRationale() {

    }

    fun assertingSingleClauseToClauseDatabaseTailMaintainsIndexingRationale() {

    }

    fun retractingSingleClauseFromClauseDatabaseMaintainsIndexingRationale() {

    }

    fun retractingMultipleClausesFromClauseDatabaseMaintainsIndexingRationale() {

    }

    private fun indexingInvariantsHolds(expected: KtList<Clause>, tested: KtList<Clause>): Boolean {
        val i = expected.iterator()
        val j = tested.iterator()
    }
*/

    //    fun indexingInvariantsHoldsTrueOnCustomFactsUsage() {
//        val customClauseDatabase = clauseDatabaseGenerator(
//            listOf(
//                Scope.empty{ factOf(structOf("f", varOf("X")))  },
//                Scope.empty{ factOf(structOf("f", atomOf("a"))) },
//                Scope.empty{ factOf(structOf("f", atomOf("b"))) },
//                Scope.empty{ factOf(structOf("f", varOf("Y"))) }
//            )
//        )
//
//        val expectedIndexing =
//            listOf(
//                Fact.of(Struct.of("f", Var.of("X"))),
//                Fact.of(Struct.of("f", Atom.of("a"))),
//                Fact.of(Struct.of("f", Atom.of("b"))),
//                Fact.of(Struct.of("f", Var.of("Y")))
//            ).asIterable()
//        testVisualisation(expectedIndexing, customClauseDatabase.clauses)
//        assertClausesHaveSameLengthAndContent(expectedIndexing, customClauseDatabase.clauses)
//    }
//
//    fun indexingInvariantsHoldsTrueOnCustomFactsRepetition() {
//        val customClauseDatabase = clauseDatabaseGenerator(
//            listOf(
//                Scope.empty{ factOf(structOf("f", varOf("X")))  },
//                Scope.empty{ factOf(structOf("f", atomOf("a"))) },
//                Scope.empty{ factOf(structOf("f", atomOf("b"))) },
//                Scope.empty{ factOf(structOf("f", varOf("Y"))) },
//                Scope.empty{ factOf(structOf("f", varOf("X")))  },
//                Scope.empty{ factOf(structOf("f", atomOf("a"))) },
//                Scope.empty{ factOf(structOf("f", atomOf("b"))) },
//                Scope.empty{ factOf(structOf("f", varOf("Y"))) }
//            )
//        )
//
//        val expectedIndexing =
//            listOf(
//                Fact.of(Struct.of("f", Var.of("X"))),
//                Fact.of(Struct.of("f", Atom.of("a"))),
//                Fact.of(Struct.of("f", Atom.of("b"))),
//                Fact.of(Struct.of("f", Var.of("Y"))),
//                Fact.of(Struct.of("f", Var.of("X"))),
//                Fact.of(Struct.of("f", Atom.of("a"))),
//                Fact.of(Struct.of("f", Atom.of("b"))),
//                Fact.of(Struct.of("f", Var.of("Y")))
//            ).asIterable()
//
//        testVisualisation(expectedIndexing, customClauseDatabase.clauses)
//        assertClausesHaveSameLengthAndContent(expectedIndexing, customClauseDatabase.clauses)
//    }


}