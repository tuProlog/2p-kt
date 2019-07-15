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
        assertMguCorrect(UnificationUtils.successfulUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, true) }
        assertMguCorrect(UnificationUtils.successfulUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
    }

    @Test
    fun matchWorksAsExpectedWithPositiveExamples() {
        assertMatchCorrect(UnificationUtils.successfulUnifications) { term1, term2 -> myStrategy.match(term1, term2, true) }
        assertMatchCorrect(UnificationUtils.successfulUnifications) { term1, term2 -> myStrategy.match(term1, term2, false) }
    }

    @Test
    fun unifyWorksAsExpectedWithPositiveExamples() {
        assertUnifiedTermCorrect(UnificationUtils.successfulUnifications) { term1, term2 -> myStrategy.unify(term1, term2, true) }
        assertUnifiedTermCorrect(UnificationUtils.successfulUnifications) { term1, term2 -> myStrategy.unify(term1, term2, false) }
    }

    @Test
    fun mguWorksAsExpectedWithNegativeExamples() {
        assertMguCorrect(UnificationUtils.failedUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, true) }
        assertMguCorrect(UnificationUtils.failedUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
    }

    @Test
    fun matchWorksAsExpectedWithNegativeExamples() {
        assertMatchCorrect(UnificationUtils.failedUnifications) { term1, term2 -> myStrategy.match(term1, term2, true) }
        assertMatchCorrect(UnificationUtils.failedUnifications) { term1, term2 -> myStrategy.match(term1, term2, false) }
    }

    @Test
    fun unifyWorksAsExpectedWithNegativeExamples() {
        assertUnifiedTermCorrect(UnificationUtils.failedUnifications) { term1, term2 -> myStrategy.unify(term1, term2, true) }
        assertUnifiedTermCorrect(UnificationUtils.failedUnifications) { term1, term2 -> myStrategy.unify(term1, term2, false) }
    }

    @Test
    fun mguWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMguCorrect(UnificationUtils.occurCheckFailedUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, true) }
    }

    @Test
    fun matchWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMatchCorrect(UnificationUtils.occurCheckFailedUnifications) { term1, term2 -> myStrategy.match(term1, term2, true) }
    }

    @Test
    fun unifyWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertUnifiedTermCorrect(UnificationUtils.occurCheckFailedUnifications) { term1, term2 -> myStrategy.unify(term1, term2, true) }
    }

    @Test
    fun occurCheckDisabledTesting() {
        val xVar = Var.of("X")
        val structWithX = Struct.of("f", xVar)

        val correctnessMap = mapOf((xVar `=` structWithX) to
                Triple(Substitution.of(xVar, structWithX), true, structWithX))

        assertMguCorrect(correctnessMap) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
        assertMatchCorrect(correctnessMap) { term1, term2 -> myStrategy.match(term1, term2, false) }
        assertUnifiedTermCorrect(correctnessMap) { term1, term2 -> myStrategy.unify(term1, term2, false) }
    }

    @Test
    fun sequenceOfEquationsSuccessUnification() {
        forEquationSequence(::assertMguCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(::assertMguCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }


        forEquationSequence(::assertMatchCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(::assertMatchCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }


        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }

        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
    }

    @Test
    fun sequenceOfEquationsFailedUnification() {
        forEquationSequence(::assertMguCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(::assertMguCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }


        forEquationSequence(::assertMatchCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(::assertMatchCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }


        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }

        forEquationSequence(::assertUnifiedTermCorrect, UnificationUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
    }
}
