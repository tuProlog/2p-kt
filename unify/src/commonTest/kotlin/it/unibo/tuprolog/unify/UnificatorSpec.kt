package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
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
            Unificator.naive().context shouldBe Substitution.empty()
        }

        test("passing-naive-a-context-this-will-be-the-strategy-context") {
            Unificator.naive(myExampleContext).context shouldBe myExampleContext
            Unificator.naive(Substitution.failed()).context shouldBe Substitution.failed()
        }

        test("strict-without-args-create-unification-strategy-with-empty-context") {
            Unificator.strict().context shouldBe Substitution.empty()
        }

        test("passing-strict-a-context-this-will-be-the-strategy-context") {
            Unificator.strict(myExampleContext).context shouldBe myExampleContext
            Unificator.strict(Substitution.failed()).context shouldBe Substitution.failed()
        }

        test("naive-strategy-compares-numbers-by-value") {
            Unificator.naive().mgu(Integer.of(1), Real.of(1.0)) shouldBe Substitution.empty()
        }

        test("strict-strategy-compares-all-data-types-with-equals") {
            val firstA = Var.of("A")
            val secondA = Var.of("A")

            Unificator.strict().mgu(firstA, secondA) shouldBe Substitution.of(firstA to secondA)
            Unificator.strict().mgu(aVar, aVar) shouldBe Substitution.empty()

            Unificator.strict().mgu(Integer.of(1), Real.of(1.0)) shouldBe Substitution.failed()
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
