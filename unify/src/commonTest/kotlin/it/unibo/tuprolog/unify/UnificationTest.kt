package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unification.Companion.matches
import it.unibo.tuprolog.unify.Unification.Companion.mguWith
import it.unibo.tuprolog.unify.Unification.Companion.unifyWith
import it.unibo.tuprolog.unify.testutils.UnificationUtils
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificationUtils.assertUnifiedTermCorrect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Unification] companion object
 *
 * @author Enrico
 */
internal class UnificationTest {

    private val aVar = Var.of("A")
    private val myExampleContext = Substitution.of("A", Atom.of("a"))

    @Test
    fun naiveWithoutArgsCreateUnificationStrategyWithEmptyContext() {
        assertEquals(Substitution.empty(), Unification.naive().context)
    }

    @Test
    fun passingNaiveAContextThisWillBeTheStrategyContext() {
        assertEquals(myExampleContext, Unification.naive(myExampleContext).context)
        assertEquals(Substitution.failed(), Unification.naive(Substitution.failed()).context)
    }

    @Test
    fun strictWithoutArgsCreateUnificationStrategyWithEmptyContext() {
        assertEquals(Substitution.empty(), Unification.strict().context)
    }

    @Test
    fun passingStrictAContextThisWillBeTheStrategyContext() {
        assertEquals(myExampleContext, Unification.strict(myExampleContext).context)
        assertEquals(Substitution.failed(), Unification.strict(Substitution.failed()).context)
    }

    @Test
    fun naiveStrategyComparesNumbersByValue() {
        assertEquals(Substitution.empty(), Unification.naive().mgu(Integer.of(1), Real.of(1.0)))
    }

    @Test
    fun strictStrategyComparesAllDataTypesWithEquals() {
        val firstA = Var.of("A")
        val secondA = Var.of("A")

        assertEquals(Substitution.of(firstA to secondA), Unification.strict().mgu(firstA, secondA))
        assertEquals(Substitution.empty(), Unification.strict().mgu(aVar, aVar))

        assertEquals(Substitution.failed(), Unification.strict().mgu(Integer.of(1), Real.of(1.0)))
    }

    @Test
    fun mguWithFunctionWorksAsExpected() {
        val toTestUnification = UnificationUtils.successfulUnifications + UnificationUtils.failedUnifications

        assertMguCorrect(toTestUnification) { term1, term2 -> term1 mguWith term2 }
    }

    @Test
    fun matchesFunctionWorksAsExpected() {
        val toTestUnification = UnificationUtils.successfulUnifications + UnificationUtils.failedUnifications

        assertMatchCorrect(toTestUnification) { term1, term2 -> term1 matches term2 }
    }

    @Test
    fun unifyWithFunctionWorksAsExpected() {
        val toTestUnification = UnificationUtils.successfulUnifications + UnificationUtils.failedUnifications

        assertUnifiedTermCorrect(toTestUnification) { term1, term2 -> term1 unifyWith term2 }
    }
}
