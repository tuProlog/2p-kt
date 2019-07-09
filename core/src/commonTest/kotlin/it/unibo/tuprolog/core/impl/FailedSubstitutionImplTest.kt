package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.StructUtils
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [FailedSubstitutionImpl] and [Substitution]
 *
 * @author Enrico
 */
internal class FailedSubstitutionImplTest {

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
}
