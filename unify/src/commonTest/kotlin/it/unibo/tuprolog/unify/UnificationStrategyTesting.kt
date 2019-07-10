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
        assertMguCorrect(UnificationUtils.successfulUnifications, naiveStrategy, true)
        assertMguCorrect(UnificationUtils.successfulUnifications, naiveStrategy, false)
    }

    @Test
    fun matchWorksAsExpectedWithPositiveExamples() {
        assertMatchCorrect(UnificationUtils.successfulUnifications, naiveStrategy, true)
        assertMatchCorrect(UnificationUtils.successfulUnifications, naiveStrategy, false)
    }

    @Test
    fun unifyWorksAsExpectedWithPositiveExamples() {
        assertUnifiedTermCorrect(UnificationUtils.successfulUnifications, naiveStrategy, true)
        assertUnifiedTermCorrect(UnificationUtils.successfulUnifications, naiveStrategy, false)
    }

    @Test
    fun mguWorksAsExpectedWithNegativeExamples() {
        assertMguCorrect(UnificationUtils.failedUnifications, naiveStrategy, true)
        assertMguCorrect(UnificationUtils.failedUnifications, naiveStrategy, false)
    }

    @Test
    fun matchWorksAsExpectedWithNegativeExamples() {
        assertMatchCorrect(UnificationUtils.failedUnifications, naiveStrategy, true)
        assertMatchCorrect(UnificationUtils.failedUnifications, naiveStrategy, false)
    }

    @Test
    fun unifyWorksAsExpectedWithNegativeExamples() {
        assertUnifiedTermCorrect(UnificationUtils.failedUnifications, naiveStrategy, true)
        assertUnifiedTermCorrect(UnificationUtils.failedUnifications, naiveStrategy, false)
    }

    @Test
    fun mguWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMguCorrect(UnificationUtils.occurCheckFailedUnifications, naiveStrategy, true)
    }

    @Test
    fun matchWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMatchCorrect(UnificationUtils.occurCheckFailedUnifications, naiveStrategy, true)
    }

    @Test
    fun unifyWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertUnifiedTermCorrect(UnificationUtils.occurCheckFailedUnifications, naiveStrategy, true)
    }

    @Test
    fun occurCheckDisabledTesting() {
        val xVar = Var.of("X")
        val structWithX = Struct.of("f", xVar)

        val correctnessMap = mapOf((xVar `=` structWithX) to
                Triple(Substitution.of(xVar, structWithX), true, structWithX))

        assertMguCorrect(correctnessMap, naiveStrategy, false)
        assertMatchCorrect(correctnessMap, naiveStrategy, false)
        assertUnifiedTermCorrect(correctnessMap, naiveStrategy, false)
    }
}
