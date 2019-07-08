package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Substitution.Companion.asSuccessSubstitution
import it.unibo.tuprolog.core.impl.FailedSubstitutionImpl
import it.unibo.tuprolog.core.impl.SuccessSubstitutionImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for [Substitution] companion object
 *
 * @author Enrico
 */
internal class SubstitutionTest {

    private val correctInstances by lazy { SubstitutionUtils.mixedSubstitutions.map(::SuccessSubstitutionImpl) }

    @Test
    fun failedShouldReturnTheFailedSubstitutionInstance() {
        assertEquals(FailedSubstitutionImpl, Substitution.failed())
        assertSame(FailedSubstitutionImpl, Substitution.failed())
    }

    @Test
    fun emptyReturnsAnEmptySubstitution() {
        assertTrue(Substitution.empty().isEmpty())
    }

    @Test
    fun emptyReturnsSuccessfulSubstitution() {
        assertTrue(Substitution.empty() is SuccessSubstitutionImpl)
    }

    @Test
    fun asSuccessSubstitutionConvertsAMapVarTermToSubstitution() {
        val toBeTested = SubstitutionUtils.mixedSubstitutions.map { it.asSuccessSubstitution() }

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
    fun ofSubstitutions() {
        val correct = SuccessSubstitutionImpl(SubstitutionUtils.mixedSubstitutions.reduce { map1, map2 -> map1 + map2 })
        val toBeTested = Substitution.of(correctInstances.first(), *correctInstances.dropFirst().toTypedArray())

        assertEquals(correct, toBeTested)
    }
}
