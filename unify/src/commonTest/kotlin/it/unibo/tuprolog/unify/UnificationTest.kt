package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unification.Companion.matches
import it.unibo.tuprolog.unify.Unification.Companion.mguWith
import it.unibo.tuprolog.unify.Unification.Companion.unifyWith
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Unification] companion object
 *
 * @author Enrico
 */
internal class UnificationTest {

    private val aVar = Var.of("A")
    private val aAtom = Atom.of("a")
    private val myExampleContext = Substitution.of("A", Atom.of("a"))

    @Test
    fun naiveCreatesAUnificationStrategyThatUsesEqualsToCheckTermIdentity() {
        assertEquals(Substitution.empty(), Unification.naive().mgu(Var.of("A"), Var.of("A")))
        assertEquals(Substitution.empty(), Unification.naive().mgu(aVar, aVar))
    }

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
    fun strictCreatesAUnificationStrategyThatUsesStrictlyEqualsToCheckTermIdentity() {
        val firstA = Var.of("A")
        val secondA = Var.of("A")

        assertEquals(Substitution.of(firstA to secondA), Unification.strict().mgu(firstA, secondA))
        assertEquals(Substitution.empty(), Unification.strict().mgu(aVar, aVar))
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
    fun defaultUnificationStrategyUsesEqualsToCheckTermsIdentity() {
        assertEquals(Substitution.empty(), Unification.default.mgu(Var.of("A"), Var.of("A")))
        assertEquals(Substitution.empty(), Unification.default.mgu(aVar, aVar))
    }

    @Test
    fun mguWithFunctionWorksAsExpected() {
        assertEquals(Unification.default.mgu(aVar, aAtom), aVar mguWith aAtom)
    }

    @Test
    fun matchesFunctionWorksAsExpected() {
        assertEquals(Unification.default.match(aVar, aAtom), aVar matches aAtom)
    }

    @Test
    fun unifyWithFunctionWorksAsExpected() {
        assertEquals(Unification.default.unify(aVar, aAtom), aVar unifyWith aAtom)
    }
}
