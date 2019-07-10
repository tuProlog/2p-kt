package it.unibo.tuprolog.unify

import `=`
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.UnificationUtils
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertUnifiedTermCorrect
import kotlin.test.Test

internal class UnificationStrategyTesting {

    private val naiveStrategy = Unification.naive()

    @Test
    fun mguWorksAsExpectedWithPositiveExamples() {
        assertMguCorrect(naiveStrategy, UnificationUtils.successfulUnificationMgus, true)
        assertMguCorrect(naiveStrategy, UnificationUtils.successfulUnificationMgus, false)
    }

    @Test
    fun matchWorksAsExpectedWithPositiveExamples() {
        assertMatchCorrect(naiveStrategy, UnificationUtils.successfulUnificationMatches, true)
        assertMatchCorrect(naiveStrategy, UnificationUtils.successfulUnificationMatches, false)
    }

    @Test
    fun unifyWorksAsExpectedWithPositiveExamples() {
        assertUnifiedTermCorrect(naiveStrategy, UnificationUtils.successfulUnificationTerms, true)
        assertUnifiedTermCorrect(naiveStrategy, UnificationUtils.successfulUnificationTerms, false)
    }

    @Test
    fun mguWorksAsExpectedWithNegativeExamples() {
        assertMguCorrect(naiveStrategy, UnificationUtils.failedUnificationMgus, true)
        assertMguCorrect(naiveStrategy, UnificationUtils.failedUnificationMgus, false)
    }

    @Test
    fun matchWorksAsExpectedWithNegativeExamples() {
        assertMatchCorrect(naiveStrategy, UnificationUtils.failedUnificationMatches, true)
        assertMatchCorrect(naiveStrategy, UnificationUtils.failedUnificationMatches, false)
    }

    @Test
    fun unifyWorksAsExpectedWithNegativeExamples() {
        assertUnifiedTermCorrect(naiveStrategy, UnificationUtils.failedUnificationTerms, true)
        assertUnifiedTermCorrect(naiveStrategy, UnificationUtils.failedUnificationTerms, false)
    }

    @Test
    fun mguWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMguCorrect(naiveStrategy, UnificationUtils.occurCheckFailedUnificationMgus, true)
    }

    @Test
    fun matchWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMatchCorrect(naiveStrategy, UnificationUtils.occurCheckFailedUnificationMatches, true)
    }

    @Test
    fun unifyWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertUnifiedTermCorrect(naiveStrategy, UnificationUtils.occurCheckFailedUnificationTerms, true)
    }

    @Test
    fun occurCheckDisabledTesting() {
        val xVar = Var.of("X")
        val structWithX = Struct.of("f", xVar)

        val correctMguMap = mapOf((xVar `=` structWithX) to Substitution.of(xVar, structWithX))
        val correctMatchMap = mapOf((xVar `=` structWithX) to true)
        val correctUnifiedTermMap = mapOf((xVar `=` structWithX) to structWithX)

        assertMguCorrect(naiveStrategy, correctMguMap, false)
        assertMatchCorrect(naiveStrategy, correctMatchMap, false)
        assertUnifiedTermCorrect(naiveStrategy, correctUnifiedTermMap, false)
    }
}
