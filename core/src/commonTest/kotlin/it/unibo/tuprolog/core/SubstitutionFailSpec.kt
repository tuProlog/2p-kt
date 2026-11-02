package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.testutils.StructUtils

/**
 * Test class for [Substitution.Fail] and [Substitution]
 *
 * @author Enrico
 */
class SubstitutionFailSpec :
    FunSpec({
        val failedSubstitution = Substitution.failed()

        test("failed-substitution-is-empty") {
            failedSubstitution.isEmpty().shouldBeTrue()
        }

        test("is-failed-is-true") {
            failedSubstitution.isFailed.shouldBeTrue()
        }

        test("is-success-is-false") {
            failedSubstitution.isSuccess.shouldBeFalse()
        }

        withTests(nameFn = { "apply-to-returns-null/${it.id}" }, StructUtils.mixedStructCases) { case ->
            failedSubstitution.applyTo(Struct.of(case.functor, case.args)) shouldBe null
        }

        test("failed-substitution-plus-other-substitution-returns-always-failed-substitution") {
            (failedSubstitution + Substitution.of("A", Truth.TRUE)) shouldBe failedSubstitution
            (Substitution.of("A", Truth.TRUE) + failedSubstitution) shouldBe failedSubstitution
        }

        test("failed-substitution-minus-other-substitution-keys-returns-always-failed-substitution") {
            (failedSubstitution - Substitution.empty().keys) shouldBe failedSubstitution
            (failedSubstitution - listOf(Var.anonymous())) shouldBe failedSubstitution
        }

        test("failed-substitution-minus-other-substitution-returns-always-failed-substitution") {
            (failedSubstitution - Substitution.empty()) shouldBe failedSubstitution
            (failedSubstitution - failedSubstitution) shouldBe failedSubstitution
        }

        test("failed-substitution-filtering-entries-returns-always-failed-substitution") {
            (failedSubstitution.filter { (_, _) -> true }) shouldBe failedSubstitution
            (failedSubstitution.filter { (_, _) -> false }) shouldBe failedSubstitution
        }

        test("failed-substitution-filtering-with-parameters-predicate-returns-always-failed-substitution") {
            (failedSubstitution.filter { _, _ -> true }) shouldBe failedSubstitution
            (failedSubstitution.filter { _, _ -> false }) shouldBe failedSubstitution
        }
    })
