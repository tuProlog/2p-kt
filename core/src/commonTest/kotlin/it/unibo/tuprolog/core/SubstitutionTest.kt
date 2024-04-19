package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [Substitution] companion object
 *
 * @author Enrico
 */
internal class SubstitutionTest {
    private val aVar = Var.of("A")
    private val bVar = Var.of("B")

    private val correctInstances by lazy { SubstitutionUtils.mixedSubstitutions.map(Substitution.Companion::of) }

    @Test
    fun failedShouldReturnTheFailedSubstitutionInstance() {
        assertEquals(Substitution.failed(), Substitution.failed())
        assertSame(Substitution.failed(), Substitution.failed())
    }

    @Test
    fun failedSubstitutionIsFailed() {
        assertTrue(Substitution.failed().isFailed)
    }

    @Test
    fun failedSubstitutionIsNotSuccess() {
        assertFalse(Substitution.failed().isSuccess)
    }

    @Test
    fun emptyReturnsAnEmptySubstitution() {
        assertTrue(Substitution.empty().isEmpty())
    }

    @Test
    fun emptySubstitutionIsSuccess() {
        assertTrue(Substitution.empty().isSuccess)
    }

    @Test
    fun emptySubstitutionIsNotFailed() {
        assertFalse(Substitution.empty().isFailed)
    }

    @Test
    fun asUnifierConvertsAMapVarTermToSubstitution() {
        val toBeTested = SubstitutionUtils.mixedSubstitutions.map { it.asUnifier() }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun asUnifierResultsInEmptySubstitutionIfIdentityMappings() {
        assertEquals(Substitution.empty(), mapOf(aVar to aVar).asUnifier())
    }

    @Test
    fun asUnifierResultsInEmptySubstitutionIfIndirectIdentityMappings() {
        assertEquals(Substitution.empty(), mapOf(aVar to bVar, bVar to aVar).asUnifier())
    }

    @Test
    fun ofVariableAndTerm() {
        val correct = correctInstances.filter { it.size == 1 }
        val toBeTested =
            SubstitutionUtils.mixedSubstitutionsAsPairs.filter { it.size == 1 }.map { it.first() }
                .map { (variable, withTerm) -> Substitution.of(variable, withTerm) }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofStringVariableAndTerm() {
        val correct = correctInstances.filter { it.size == 1 }
        val toBeTested =
            SubstitutionUtils.mixedSubstitutionsAsPairs.filter { it.size == 1 }.map { it.first() }
                .map { (variable, withTerm) -> Substitution.of(Var.of(variable.name), withTerm) }

        onCorrespondingItems(correct, toBeTested) { expectedMap, actualMap ->
            onCorrespondingItems(expectedMap.keys, actualMap.keys, ::assertDifferentVariableExceptForName)
            assertEquals(expectedMap.values.toList(), actualMap.values.toList())
        }
    }

    @Test
    fun ofVariableTermPairs() {
        val toBeTested =
            SubstitutionUtils.mixedSubstitutionsAsPairs.map {
                Substitution.of(it.first(), *it.dropFirst().toTypedArray())
            }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofVariableTermPairsDoesntComplainIfExactDuplicates() {
        val toBeTested =
            SubstitutionUtils.duplicatedPairSubstitution.map {
                Substitution.of(it.first(), *it.dropFirst().toTypedArray())
            }

        val correctInstances = SubstitutionUtils.duplicatedPairSubstitution.map { Substitution.of(it.toMap()) }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofVariableTermPairsResultInFailedSubstitutionIfContradicting() {
        val toBeTested =
            SubstitutionUtils.contradictingSubstitutions.map {
                Substitution.of(it.first(), *it.dropFirst().toTypedArray())
            }

        toBeTested.forEach { assertEquals(Substitution.failed(), it) }
    }

    @Test
    fun ofIterableVariableTermPairs() {
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.map { Substitution.of(it) }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofIterableVariableTermPairsDoesntComplainIfExactDuplicates() {
        val toBeTested = SubstitutionUtils.duplicatedPairSubstitution.map { Substitution.of(it) }

        val correctInstances = SubstitutionUtils.duplicatedPairSubstitution.map { Substitution.unifierOf(it.toMap()) }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofIterableVariableTermPairsResultInFailedSubstitutionIfContradicting() {
        val toBeTested = SubstitutionUtils.contradictingSubstitutions.map { Substitution.of(it) }

        toBeTested.forEach { assertEquals(Substitution.failed(), it) }
    }

    @Test
    fun ofEmptyIterable() {
        assertEquals(Substitution.empty(), Substitution.of(emptyList()))
    }

    @Test
    fun ofSubstitutions() {
        val correct = Substitution.unifierOf(SubstitutionUtils.mixedSubstitutions.reduce { map1, map2 -> map1 + map2 })
        val toBeTested = Substitution.merge(correctInstances.first(), *correctInstances.dropFirst().toTypedArray())

        assertEquals(correct, toBeTested)
    }

    @Test
    fun ofSubstitutionsWithAlwaysSameSubstitutionsReturnsThemNotDuplicated() {
        Scope.of("A", "B") {
            val correct =
                Substitution.unifierOf(
                    mapOf(
                        varOf("A") to atomOf("a"),
                        varOf("B") to atomOf("b"),
                    ),
                )

            val toBeTested =
                Substitution.merge(
                    Substitution.unifierOf(mapOf(varOf("B") to atomOf("b"))),
                    Substitution.unifierOf(mapOf(varOf("A") to atomOf("a"))),
                    Substitution.unifierOf(mapOf(varOf("B") to atomOf("b"))),
                    Substitution.unifierOf(mapOf(varOf("A") to atomOf("a"))),
                    Substitution.unifierOf(mapOf(varOf("B") to atomOf("b"))),
                )

            assertEquals(correct, toBeTested)
        }
    }

    @Test
    fun ofSubstitutionsWithContradictingOnesReturnsFailed() {
        Scope.of("A", "B") {
            val toBeTested =
                Substitution.merge(
                    Substitution.unifierOf(mapOf(varOf("B") to atomOf("f"))),
                    Substitution.unifierOf(mapOf(varOf("B") to varOf("A"))),
                    Substitution.unifierOf(mapOf(varOf("A") to atomOf("b"))),
                    Substitution.unifierOf(mapOf(varOf("A") to atomOf("a"))),
                    Substitution.unifierOf(mapOf(varOf("B") to atomOf("b"))),
                )

            assertEquals(Substitution.failed(), toBeTested)
        }
    }

    @Test
    fun ofSubstitutionsCompliesWithCompositionDefinedInPrologStandard() {
        val xAtom = Atom.of("x")

        val first = Substitution.unifierOf(mapOf(bVar to Struct.of("f", aVar)))
        val second = Substitution.unifierOf(mapOf(aVar to xAtom))

        val firstComposedSecondExpected =
            Substitution.unifierOf(
                mapOf(
                    bVar to Struct.of("f", xAtom),
                    aVar to xAtom,
                ),
            )

        val secondComposedFirstExpected =
            Substitution.unifierOf(
                mapOf(
                    aVar to xAtom,
                    bVar to Struct.of("f", aVar),
                ),
            )

        assertEquals(firstComposedSecondExpected, Substitution.merge(first, second))
        assertEquals(secondComposedFirstExpected, Substitution.merge(second, first))
    }

    @Test
    fun ofImplementationsReturnEmptySubstitutionIfIdentityPassedIn() {
        Scope.empty {
            val identityPairA = varOf("A") to varOf("A")
            val identityPairB = varOf("B") to varOf("B")
            val toBeTested =
                listOf(
                    Substitution.of(identityPairA.first, identityPairA.second),
                    Substitution.of(identityPairA, identityPairB),
                    Substitution.of(listOf(identityPairA, identityPairB)),
                    Substitution.merge(Substitution.of(identityPairA), Substitution.of(identityPairB)),
                )

            toBeTested.forEach { assertEquals(Substitution.empty(), it) }
        }
    }

    @Test
    fun substitutionMergeFailsInAssignmentChains() {
        Scope.empty {
            val base = unifierOf(varOf("X") to varOf("Y"), varOf("Y") to varOf("Z"))
            val a = atomOf("a")

            val assignment1 = unifierOf(varOf("X"), a)
            val assignment2 = unifierOf(varOf("Y") to a)
            val assignment3 = unifierOf(varOf("Z") to a)

            assertEquals(Substitution.failed(), base + assignment1)
            assertEquals(Substitution.failed(), base + assignment2)
            assertEquals(unifierOf(varOf("X") to a, varOf("Y") to a, varOf("Z") to a), base + assignment3)
        }
    }
}
