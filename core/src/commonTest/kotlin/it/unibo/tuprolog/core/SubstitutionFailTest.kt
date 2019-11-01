package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.StructUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [Substitution.Fail] and [Substitution]
 *
 * @author Enrico
 */
internal class SubstitutionFailTest {

    private val failedSubstitution = Substitution.Fail

    @Test
    fun failedSubstitutionIsEmpty() {
        assertTrue(failedSubstitution.isEmpty())
    }

    @Test
    fun isFailedIsTrue() {
        assertTrue(failedSubstitution.isFailed)
    }

    @Test
    fun isSuccessIsFalse() {
        assertFalse(failedSubstitution.isSuccess)
    }

    @Test
    fun failedSubstitutionApplicationDoesntChangeTerms() {
        val someTerms = StructUtils.mixedStructs.map { (functor, args) -> Struct.of(functor, *args) }

        someTerms.forEach { term -> assertEqualities(term, failedSubstitution.applyTo(term)) }
    }

    @Test
    fun failedSubstitutionPlusOtherSubstitutionReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution + Substitution.of("A", Truth.`true`()))
        assertEquals(failedSubstitution, Substitution.of("A", Truth.`true`()) + failedSubstitution)
    }

    @Test
    fun failedSubstitutionMinusOtherSubstitutionReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution - Substitution.empty())
        assertEquals(failedSubstitution, failedSubstitution - failedSubstitution)
    }

    @Test
    fun failedSubstitutionFilteringReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution.filter { (_, _) -> true })
        assertEquals(failedSubstitution, failedSubstitution.filter { (_, _) -> false })
    }
}
