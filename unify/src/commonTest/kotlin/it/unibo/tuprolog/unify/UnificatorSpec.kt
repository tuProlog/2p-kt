package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import kotlin.test.assertEquals

/**
 * Test class for [Unificator] companion object
 *
 * @author Enrico
 */
class UnificatorSpec :
    FunSpec({
        val aVar = Var.of("A")

        val myExampleContext = Substitution.of("A", Atom.of("a"))

        test("naive-without-args-create-unification-strategy-with-empty-context") {
            assertEquals(Substitution.empty(), Unificator.naive().context)
        }

        test("passing-naive-a-context-this-will-be-the-strategy-context") {
            assertEquals(myExampleContext, Unificator.naive(myExampleContext).context)
            assertEquals(Substitution.failed(), Unificator.naive(Substitution.failed()).context)
        }

        test("strict-without-args-create-unification-strategy-with-empty-context") {
            assertEquals(Substitution.empty(), Unificator.strict().context)
        }

        test("passing-strict-a-context-this-will-be-the-strategy-context") {
            assertEquals(myExampleContext, Unificator.strict(myExampleContext).context)
            assertEquals(Substitution.failed(), Unificator.strict(Substitution.failed()).context)
        }

        test("naive-strategy-compares-numbers-by-value") {
            assertEquals(Substitution.empty(), Unificator.naive().mgu(Integer.of(1), Real.of(1.0)))
        }

        test("strict-strategy-compares-all-data-types-with-equals") {
            val firstA = Var.of("A")
            val secondA = Var.of("A")

            assertEquals(Substitution.of(firstA to secondA), Unificator.strict().mgu(firstA, secondA))
            assertEquals(Substitution.empty(), Unificator.strict().mgu(aVar, aVar))

            assertEquals(Substitution.failed(), Unificator.strict().mgu(Integer.of(1), Real.of(1.0)))
        }

        val examples = UnificatorUtils.successfulUnifications + UnificatorUtils.failedUnifications

        withTests(nameFn = { "mgu-with/${it.id}" }, examples) { case ->
            assertMguCorrect(case) { term1, term2 -> term1 mguWith term2 }
        }

        withTests(nameFn = { "matches/${it.id}" }, examples) { case ->
            assertMatchCorrect(case) { term1, term2 -> term1 matches term2 }
        }

        withTests(nameFn = { "unify-with/${it.id}" }, examples) { case ->
            assertUnifiedTermCorrect(case) { term1, term2 -> term1 unifyWith term2 }
        }
    })
