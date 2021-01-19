package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SubstitutionImpl
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

    private val failedSubstitution = SubstitutionImpl.FailImpl

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
    fun failedSubstitutionApplicationReturnsNull() {
        val someTerms = StructUtils.mixedStructs.map { (functor, args) -> Struct.of(functor, *args) }

        someTerms.forEach { term -> assertEquals(null, failedSubstitution.applyTo(term)) }
    }

    @Test
    fun failedSubstitutionPlusOtherSubstitutionReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution + Substitution.of("A", Truth.TRUE))
        assertEquals(failedSubstitution, Substitution.of("A", Truth.TRUE) + failedSubstitution)
    }

    @Test
    fun failedSubstitutionMinusOtherSubstitutionKeysReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution - Substitution.empty().keys)
        assertEquals(failedSubstitution, failedSubstitution - listOf(Var.anonymous()))
    }

    @Test
    fun failedSubstitutionMinusOtherSubstitutionReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution - Substitution.empty())
        assertEquals(failedSubstitution, failedSubstitution - failedSubstitution)
    }

    @Test
    fun failedSubstitutionFilteringEntriesReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution.filter { (_, _) -> true })
        assertEquals(failedSubstitution, failedSubstitution.filter { (_, _) -> false })
    }

    @Test
    fun failedSubstitutionFilteringWithParametersPredicateReturnsAlwaysFailedSubstitution() {
        assertEquals(failedSubstitution, failedSubstitution.filter { _, _ -> true })
        assertEquals(failedSubstitution, failedSubstitution.filter { _, _ -> false })
    }
}
