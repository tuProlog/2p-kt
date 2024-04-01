package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.UnificatorUtils
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertUnifiedTermCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.forEquationSequence
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.memberClause
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.negativeMemberPatterns
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.positiveMemberPatterns
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List.Companion as LogicList

/**
 * Test class for [AbstractUnificator]
 *
 * @author Enrico
 */
internal class AbstractUnificatorTest {
    /** A concrete strategy constructor */
    private val myStrategyConstructor: (Substitution) -> Unificator = {
        object : AbstractUnificator(it) {
            override fun checkTermsEquality(
                first: Term,
                second: Term,
            ): Boolean = first == second
        }
    }

    /** A concrete strategy to test abstract class behaviour */
    private val myStrategy = myStrategyConstructor(Substitution.empty())

    @Test
    fun contextPropertyWorksAsExpected() {
        val mySubstitution = Substitution.of(Var.of("A"), Atom.of("a"))

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

        val correctnessMap =
            mapOf(
                (xVar eq structWithX) to
                    Triple(Substitution.of(xVar, structWithX), true, structWithX),
            )

        assertMguCorrect(correctnessMap) { term1, term2 -> myStrategy.mgu(term1, term2, false) }
        assertMatchCorrect(correctnessMap) { term1, term2 -> myStrategy.match(term1, term2, false) }
        assertUnifiedTermCorrect(correctnessMap) { term1, term2 -> myStrategy.unify(term1, term2, false) }
    }

    @Test
    fun sequenceOfEquationsSuccessUnification() {
        forEquationSequence(
            ::assertMguCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(
            ::assertMguCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(
            ::assertMatchCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(
            ::assertMatchCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }

        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.successSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
    }

    @Test
    fun testPositiveCornerCases() {
        for (pattern in positiveMemberPatterns) {
            with(myStrategyConstructor(Substitution.empty())) {
                assertTrue {
                    match(pattern, memberClause)
                }
            }
        }
    }

    @Test
    fun testNegativeCornerCases() {
        for (pattern in negativeMemberPatterns) {
            with(myStrategyConstructor(Substitution.empty())) {
                assertFalse {
                    match(pattern, memberClause)
                }
            }
        }
    }

    @Test
    fun sequenceOfEquationsFailedUnification() {
        forEquationSequence(
            ::assertMguCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(
            ::assertMguCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).mgu(t1, t2) }

        forEquationSequence(
            ::assertMatchCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(
            ::assertMatchCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).match(t1, t2) }

        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }

        forEquationSequence(
            ::assertUnifiedTermCorrect,
            UnificatorUtils.failSequenceOfUnification,
            myStrategyConstructor,
        ) { context, t1, t2 -> myStrategyConstructor(context).unify(t1, t2) }
    }

    @Test
    fun unificationWorksForBigLists() {
        val n = 10_000
        val ints = LogicList.of((0..n).map { Integer.of(it) })
        val intsAndVars = LogicList.of((0..n).map { if (it % 100 == 0) Var.anonymous() else Integer.of(it) })
        val atoms = LogicList.of((0..n).map { Atom.of("a$it") })
        val atomsAndVars = LogicList.of((0..n).map { if (it % 100 == 0) Var.anonymous() else Atom.of("a$it") })

        with(myStrategyConstructor(Substitution.empty())) {
            assertFalse { match(ints, atoms, true) }
            assertFalse { match(ints, atoms, false) }
            assertTrue { match(ints, ints, true) }
            assertTrue { match(ints, ints, false) }
            assertTrue { match(atoms, atoms, true) }
            assertTrue { match(atoms, atoms, false) }
            assertTrue { match(ints, intsAndVars, true) }
            assertTrue { match(ints, intsAndVars, false) }
            assertTrue { match(atoms, atomsAndVars, true) }
            assertTrue { match(atoms, atomsAndVars, false) }
        }
    }
}
