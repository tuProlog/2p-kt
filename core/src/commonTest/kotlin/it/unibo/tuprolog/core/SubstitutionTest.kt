package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.*

/**
 * Test class for [Substitution] companion object
 *
 * @author Enrico
 */
internal class SubstitutionTest {

    private val correctInstances by lazy { SubstitutionUtils.mixedSubstitutions.map(Substitution::Unifier) }

    @Test
    fun failedShouldReturnTheFailedSubstitutionInstance() {
        assertEquals(Substitution.Fail, Substitution.failed())
        assertSame(Substitution.Fail, Substitution.failed())
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
    fun emptyReturnsSuccessfulSubstitutionInstance() {
        assertTrue(Substitution.empty() is Substitution.Unifier)
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
    fun ofVariableAndTerm() {
        val correct = correctInstances.filter { it.size == 1 }
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.filter { it.size == 1 }.map { it.first() }
                .map { (variable, withTerm) -> Substitution.of(variable, withTerm) }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofStringVariableAndTerm() {
        val correct = correctInstances.filter { it.size == 1 }
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.filter { it.size == 1 }.map { it.first() }
                .map { (variable, withTerm) -> Substitution.of(variable.name, withTerm) }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofVariableTermPairs() {
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.map {
            Substitution.of(it.first(), *it.dropFirst().toTypedArray())
        }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofIterableVariableTermPairs() {
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.map { Substitution.of(it) }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofEmptyIterable() {
        assertEquals(Substitution.empty(), Substitution.of(emptyList()))
    }

    @Test
    fun ofSubstitutions() {
        val correct = Substitution.Unifier(SubstitutionUtils.mixedSubstitutions.reduce { map1, map2 -> map1 + map2 })
        val toBeTested = Substitution.of(correctInstances.first(), *correctInstances.dropFirst().toTypedArray())

        assertEquals(correct, toBeTested)
    }
}
