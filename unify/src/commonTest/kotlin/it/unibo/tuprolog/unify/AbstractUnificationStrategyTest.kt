package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.testutils.UnificatorUtils
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertUnifiedTermCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.forEquationSequence
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [AbstractUnificationStrategy]
 *
 * @author Enrico
 */
internal class AbstractUnificationStrategyTest {

    /** A concrete strategy constructor */
    private val myStrategyConstructor: (Substitution) -> Unificator = {
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
        assertMguCorrect(UnificatorUtils.successfulUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, true) }
        assertMguCorrect(UnificatorUtils.successfulUnifications) { term1, term2 ->
            myStrategy.mgu(term1, term2, false)
        }
    }

    @Test
    fun matchWorksAsExpectedWithPositiveExamples() {
        assertMatchCorrect(UnificatorUtils.successfulUnifications) { term1, term2 ->
            myStrategy.match(term1, term2, true)
        }
        assertMatchCorrect(UnificatorUtils.successfulUnifications) { term1, term2 ->
            myStrategy.match(term1, term2, false)
        }
    }

    @Test
    fun unifyWorksAsExpectedWithPositiveExamples() {
        assertUnifiedTermCorrect(UnificatorUtils.successfulUnifications) { term1, term2 ->
            myStrategy.unify(term1, term2, true)
        }
        assertUnifiedTermCorrect(UnificatorUtils.successfulUnifications) { term1, term2 ->
            myStrategy.unify(term1, term2, false)
        }
    }

    @Test
    fun mguWorksAsExpectedWithNegativeExamples() {
        assertMguCorrect(UnificatorUtils.failedUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, true) }
        assertMguCorrect(UnificatorUtils.failedUnifications) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
    }

    @Test
    fun matchWorksAsExpectedWithNegativeExamples() {
        assertMatchCorrect(UnificatorUtils.failedUnifications) { term1, term2 -> myStrategy.match(term1, term2, true) }
        assertMatchCorrect(UnificatorUtils.failedUnifications) { term1, term2 ->
            myStrategy.match(term1, term2, false)
        }
    }

    @Test
    fun unifyWorksAsExpectedWithNegativeExamples() {
        assertUnifiedTermCorrect(UnificatorUtils.failedUnifications) { term1, term2 ->
            myStrategy.unify(term1, term2, true)
        }
        assertUnifiedTermCorrect(UnificatorUtils.failedUnifications) { term1, term2 ->
            myStrategy.unify(term1, term2, false)
        }
    }

    @Test
    fun mguWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMguCorrect(UnificatorUtils.occurCheckFailedUnifications) { term1, term2 ->
            myStrategy.mgu(term1, term2, true)
        }
    }

    @Test
    fun matchWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertMatchCorrect(UnificatorUtils.occurCheckFailedUnifications) { term1, term2 ->
            myStrategy.match(term1, term2, true)
        }
    }

    @Test
    fun unifyWithOccurCheckEnabledStopsExecutingWithFailureOnBadEquation() {
        assertUnifiedTermCorrect(UnificatorUtils.occurCheckFailedUnifications) { term1, term2 ->
            myStrategy.unify(term1, term2, true)
        }
    }

    @Test
    fun occurCheckDisabledTesting() {
        val xVar = Var.of("X")
        val structWithX = Struct.of("f", xVar)

        val correctnessMap = mapOf(
            (xVar `=` structWithX) to
                    Triple(Substitution.of(xVar, structWithX), true, structWithX)
        )

        assertMguCorrect(correctnessMap) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
        assertMatchCorrect(correctnessMap) { term1, term2 -> myStrategy.match(term1, term2, false) }
        assertUnifiedTermCorrect(correctnessMap) { term1, term2 -> myStrategy.unify(term1, term2, false) }
    }

    @Test
    fun sequenceOfEquationsSuccessUnification() {
        forEquationSequence(::assertMguCorrect, UnificatorUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(::assertMguCorrect, UnificatorUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }


        forEquationSequence(::assertMatchCorrect, UnificatorUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(::assertMatchCorrect, UnificatorUtils.successSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }


        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }

        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
    }

    @Test
    fun sequenceOfEquationsFailedUnification() {
        forEquationSequence(::assertMguCorrect, UnificatorUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(::assertMguCorrect, UnificatorUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }


        forEquationSequence(::assertMatchCorrect, UnificatorUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(::assertMatchCorrect, UnificatorUtils.failSequenceOfUnification, myStrategyConstructor)
        { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }


        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }

        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
    }
}
