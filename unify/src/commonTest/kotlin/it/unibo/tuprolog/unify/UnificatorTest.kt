package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import it.unibo.tuprolog.unify.Unificator.Companion.unifyWith
import it.unibo.tuprolog.unify.testutils.UnificatorUtils
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMatchCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertMguCorrect
import it.unibo.tuprolog.unify.testutils.UnificatorUtils.assertUnifiedTermCorrect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Unificator] companion object
 *
 * @author Enrico
 */
internal class UnificatorTest {
    private val aVar = Var.of("A")
    private val myExampleContext = Substitution.of(Var.of("A"), Atom.of("a"))

    @Test
    fun naiveWithoutArgsCreateUnificationStrategyWithEmptyContext() {
        assertEquals(Substitution.empty(), Unificator.naive().context)
    }

    @Test
    fun passingNaiveAContextThisWillBeTheStrategyContext() {
        assertEquals(myExampleContext, Unificator.naive(myExampleContext).context)
        assertEquals(Substitution.failed(), Unificator.naive(Substitution.failed()).context)
    }

    @Test
    fun strictWithoutArgsCreateUnificationStrategyWithEmptyContext() {
        assertEquals(Substitution.empty(), Unificator.strict().context)
    }

    @Test
    fun passingStrictAContextThisWillBeTheStrategyContext() {
        assertEquals(myExampleContext, Unificator.strict(myExampleContext).context)
        assertEquals(Substitution.failed(), Unificator.strict(Substitution.failed()).context)
    }

    @Test
    fun naiveStrategyComparesNumbersByValue() {
        assertEquals(Substitution.empty(), Unificator.naive().mgu(Integer.of(1), Real.of(1.0)))
    }

    @Test
    fun strictStrategyComparesAllDataTypesWithEquals() {
        val firstA = Var.of("A")
        val secondA = Var.of("A")

        assertEquals(Substitution.of(firstA to secondA), Unificator.strict().mgu(firstA, secondA))
        assertEquals(Substitution.empty(), Unificator.strict().mgu(aVar, aVar))

        assertEquals(Substitution.failed(), Unificator.strict().mgu(Integer.of(1), Real.of(1.0)))
    }

    @Test
    fun mguWithFunctionWorksAsExpected() {
        val toTestUnification = UnificatorUtils.successfulUnifications + UnificatorUtils.failedUnifications

        assertMguCorrect(toTestUnification) { term1, term2 -> term1 mguWith term2 }
    }

    @Test
    fun matchesFunctionWorksAsExpected() {
        val toTestUnification = UnificatorUtils.successfulUnifications + UnificatorUtils.failedUnifications

        assertMatchCorrect(toTestUnification) { term1, term2 -> term1 matches term2 }
    }

    @Test
    fun unifyWithFunctionWorksAsExpected() {
        val toTestUnification = UnificatorUtils.successfulUnifications + UnificatorUtils.failedUnifications

        assertUnifiedTermCorrect(toTestUnification) { term1, term2 -> term1 unifyWith term2 }
    }
}
