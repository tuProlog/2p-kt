package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.UnificationUtils
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertUnifiedTermCorrect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractUnificationStrategy]
 *
 * @author Enrico
 */
internal class AbstractUnificationStrategyTest {

    /** A concrete strategy to test abstract class behaviour */
    private val myStrategy = object : AbstractUnificationStrategy() {
        override fun checkTermsEquality(first: Term, second: Term): Boolean = first == second
    }

    @Test
    fun contextShouldBeEmptyIfNotSpecified() {
        assertEquals(Substitution.empty(), myStrategy.context)
    }

    @Test
    fun mguWorksAsExpectedWithPositiveExamples() {
        assertMguCorrect(UnificationUtils.successfulUnifications, myStrategy, true)
        assertMguCorrect(UnificationUtils.successfulUnifications, myStrategy, false)
    }

    @Test
    fun matchWorksAsExpectedWithPositiveExamples() {
        assertMatchCorrect(UnificationUtils.successfulUnifications, myStrategy, true)
        assertMatchCorrect(UnificationUtils.successfulUnifications, myStrategy, false)
    }

    @Test
    fun unifyWorksAsExpectedWithPositiveExamples() {
        assertUnifiedTermCorrect(UnificationUtils.successfulUnifications, myStrategy, true)
        assertUnifiedTermCorrect(UnificationUtils.successfulUnifications, myStrategy, false)
    }

    @Test
    fun mguWorksAsExpectedWithNegativeExamples() {
        assertMguCorrect(UnificationUtils.failedUnifications, myStrategy, true)
        assertMguCorrect(UnificationUtils.failedUnifications, myStrategy, false)
    }

    @Test
    fun matchWorksAsExpectedWithNegativeExamples() {
        assertMatchCorrect(UnificationUtils.failedUnifications, myStrategy, true)
        assertMatchCorrect(UnificationUtils.failedUnifications, myStrategy, false)
    }

    @Test
    fun unifyWorksAsExpectedWithNegativeExamples() {
        assertUnifiedTermCorrect(UnificationUtils.failedUnifications, myStrategy, true)
        assertUnifiedTermCorrect(UnificationUtils.failedUnifications, myStrategy, false)
    }

    @Test
    fun mguWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMguCorrect(UnificationUtils.occurCheckFailedUnifications, myStrategy, true)
    }

    @Test
    fun matchWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMatchCorrect(UnificationUtils.occurCheckFailedUnifications, myStrategy, true)
    }

    @Test
    fun unifyWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertUnifiedTermCorrect(UnificationUtils.occurCheckFailedUnifications, myStrategy, true)
    }

    @Test
    fun occurCheckDisabledTesting() {
        val xVar = Var.of("X")
        val structWithX = Struct.of("f", xVar)

        val correctnessMap = mapOf((xVar `=` structWithX) to
                Triple(Substitution.of(xVar, structWithX), true, structWithX))

        assertMguCorrect(correctnessMap, myStrategy, false)
        assertMatchCorrect(correctnessMap, myStrategy, false)
        assertUnifiedTermCorrect(correctnessMap, myStrategy, false)
    }
}
