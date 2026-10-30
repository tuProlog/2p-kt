package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.testutils.StructUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [Substitution.Fail] and [Substitution]
 *
 * @author Enrico
 */
class SubstitutionFailSpec :
    FunSpec({
        val failedSubstitution = Substitution.failed()

        test("failed-substitution-is-empty") {
            assertTrue(failedSubstitution.isEmpty())
        }

        test("is-failed-is-true") {
            assertTrue(failedSubstitution.isFailed)
        }

        test("is-success-is-false") {
            assertFalse(failedSubstitution.isSuccess)
        }

        test("failed-substitution-application-returns-null") {
            val someTerms = StructUtils.mixedStructs.map { (functor, args) -> Struct.of(functor, args) }

            someTerms.forEach { term -> assertEquals(null, failedSubstitution.applyTo(term)) }
        }

        test("failed-substitution-plus-other-substitution-returns-always-failed-substitution") {
            assertEquals(failedSubstitution, failedSubstitution + Substitution.of("A", Truth.TRUE))
            assertEquals(failedSubstitution, Substitution.of("A", Truth.TRUE) + failedSubstitution)
        }

        test("failed-substitution-minus-other-substitution-keys-returns-always-failed-substitution") {
            assertEquals(failedSubstitution, failedSubstitution - Substitution.empty().keys)
            assertEquals(failedSubstitution, failedSubstitution - listOf(Var.anonymous()))
        }

        test("failed-substitution-minus-other-substitution-returns-always-failed-substitution") {
            assertEquals(failedSubstitution, failedSubstitution - Substitution.empty())
            assertEquals(failedSubstitution, failedSubstitution - failedSubstitution)
        }

        test("failed-substitution-filtering-entries-returns-always-failed-substitution") {
            assertEquals(failedSubstitution, failedSubstitution.filter { (_, _) -> true })
            assertEquals(failedSubstitution, failedSubstitution.filter { (_, _) -> false })
        }

        test("failed-substitution-filtering-with-parameters-predicate-returns-always-failed-substitution") {
            assertEquals(failedSubstitution, failedSubstitution.filter { _, _ -> true })
            assertEquals(failedSubstitution, failedSubstitution.filter { _, _ -> false })
        }
    })
