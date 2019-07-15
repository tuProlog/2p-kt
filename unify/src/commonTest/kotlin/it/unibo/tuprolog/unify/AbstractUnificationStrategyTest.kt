package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.testutils.UnificationUtils
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertUnifiedTermCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.forEquationSequence
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractUnificationStrategy]
 *
 * @author Enrico
 */
internal class AbstractUnificationStrategyTest {

    /** A concrete strategy constructor */
    private val myStrategyConstructor: (Substitution) -> Unification = {
        object : AbstractUnificationStrategy(it) {
            override fun checkTermsEquality(first: Term, second: Term): Boolean = first == second
        }
    }

    /** A concrete strategy to test abstract class behaviour */
    private val myStrategy = myStrategyConstructor(Substitution.empty())

    @Test
    fun contextPropertyWorksAsExpected() {
        val mySubstitution = Substitution.of("A", Atom.of("a"))

        assertEquals(mySubstitution, myStrategyConstructor(mySubstitution).context)
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

    @Test
    fun sequenceOfEquationsSuccessUnification() {
        forEquationSequence(::assertMguCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor, true)
        forEquationSequence(::assertMguCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor, false)

        forEquationSequence(::assertMatchCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor, true)
        forEquationSequence(::assertMatchCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor, false)

        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor, true)
        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor, false)
    }

    @Test
    fun sequenceOfEquationsFailedUnification() {
        forEquationSequence(::assertMguCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor, true)
        forEquationSequence(::assertMguCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor, false)

        forEquationSequence(::assertMatchCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor, true)
        forEquationSequence(::assertMatchCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor, false)

        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor, true)
        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor, false)
    }
}
