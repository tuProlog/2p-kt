package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Test class for [Substitution.unifier] and [Substitution]
 *
 * @author Enrico
 */
private class UnifierCase(
    val id: String,
    val substitution: Substitution,
)

class SubstitutionUnifierSpec :
    FunSpec({
        val aVar = Var.of("A")

        val bVar = Var.of("B")

        val xAtom = Atom.of("x")

        val aVarToXAtomSubstitution = Substitution.unifier(mapOf(aVar to xAtom))

        val bVarToXAtomSubstitution = Substitution.unifier(mapOf(bVar to xAtom))

        val substitutionCases =
            SubstitutionUtils.mixedSubstitutionCases.map { UnifierCase(it.id, Substitution.unifier(it.map)) } +
                listOf(UnifierCase("a-to-x", aVarToXAtomSubstitution), UnifierCase("b-to-x", bVarToXAtomSubstitution))

        test("unifier-get-by-name-retrieves-some-matching-variable") {
            val anotherVar = Var.of("A")
            val unifier =
                Substitution.unifier(
                    aVar to Integer.of(1),
                    anotherVar to Integer.of(2),
                    bVar to xAtom,
                )
            assertTrue { unifier.getByName("A") is Integer }
            assertTrue { unifier.getByName("B") is Atom }
            assertNull(unifier.getByName("C"))
        }

        test("unifier-constructor-returns-empty-unifier-if-identity-mappings-detected") {
            assertEquals(Substitution.unifier(emptyMap()), Substitution.unifier(mapOf(aVar to aVar)))
        }

        test("unifier-constructor-returns-empty-unifier-if-circular-identity-mappings-detected") {
            assertEquals(Substitution.unifier(emptyMap()), Substitution.unifier(mapOf(aVar to bVar, bVar to aVar)))
        }

        test("unifier-constructor-returns-trimmed-variable-chain") {
            val correct = Substitution.unifier(mapOf(aVar to xAtom, bVar to xAtom))
            val toBeTested = Substitution.unifier(mapOf(aVar to bVar, bVar to xAtom))
            assertEquals(correct, toBeTested)
        }

        withTests(nameFn = { "is-success-is-true/${it.id}" }, substitutionCases) { case ->
            assertTrue { case.substitution.isSuccess }
        }

        withTests(nameFn = { "is-failed-is-false/${it.id}" }, substitutionCases) { case ->
            assertFalse { case.substitution.isFailed }
        }

        withTests(
            nameFn = { "apply-to-should-substitute-variable-if-same-as-provided-substitution/${it.id}" },
            SubstitutionUtils.termsWithCases,
        ) { case ->
            assertEqualities(case.build(xAtom), aVarToXAtomSubstitution.applyTo(case.build(aVar)))
        }

        withTests(
            nameFn = { "apply-to-should-not-substitute-variable-if-not-same-as-provided-substitution/${it.id}" },
            SubstitutionUtils.termsWithCases,
        ) { case ->
            val expected = case.build(Var.of("A"))
            val actual = aVarToXAtomSubstitution.applyTo(case.build(Var.of("A")))
            assertNotEquals(expected, actual)
            assertStructurallyEquals(expected, actual)
        }

        withTests(
            nameFn = { "apply-to-should-not-substitute-not-corresponding-variable/${it.id}" },
            SubstitutionUtils.termsWithCases,
        ) { case ->
            assertEqualities(case.build(aVar), bVarToXAtomSubstitution.applyTo(case.build(aVar)))
        }

        test("equals-works-as-expected") {
            assertEquals(Substitution.unifier(mapOf(aVar to xAtom)), Substitution.unifier(mapOf(aVar to xAtom)))
            assertNotEquals(
                Substitution.unifier(mapOf(Var.of("A") to xAtom)),
                Substitution.unifier(mapOf(Var.of("A") to xAtom)),
            )
            assertNotEquals(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
        }

        test("plus-other-substitution-puts-substitutions-together") {
            val correct =
                Substitution.of((aVarToXAtomSubstitution.entries + bVarToXAtomSubstitution.entries).map { it.toPair() })
            val toBeTested = aVarToXAtomSubstitution + bVarToXAtomSubstitution

            assertEquals(correct, toBeTested)
        }

        test("plus-other-substitution-replaces-present-variables") {
            val myAtom = Atom.of("hello")
            val xVar = Var.of("X")
            val yVar = Var.of("Y")

            val correct = Substitution.of(xVar to myAtom, yVar to myAtom)
            val toBeTested = Substitution.of(xVar to yVar) + Substitution.of(yVar to myAtom)

            assertEquals(correct, toBeTested)
        }

        test("plus-other-substitution-is-idempotent-if-same-substitution") {
            assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution + aVarToXAtomSubstitution)
        }

        test("plus-other-substitution-contradicting-base-one-returns-failed-substitution") {
            val toBeTested =
                listOf(
                    Substitution.of(bVar to Truth.TRUE) + bVarToXAtomSubstitution,
                    bVarToXAtomSubstitution + Substitution.of(bVar to Truth.TRUE),
                )

            toBeTested.forEach { assertEquals(Substitution.failed(), it) }
        }

        withTests(nameFn = { "plus-failed-substitution-return-failed-substitution/${it.id}" }, substitutionCases) {
            val substitution = it.substitution
            assertEquals(Substitution.failed(), substitution + Substitution.failed())
            assertEquals(Substitution.failed(), Substitution.failed() + substitution)
        }

        test("plus-compliance-with-composition-defined-in-standard-prolog") {
            val first = Substitution.unifier(mapOf(bVar to Struct.of("f", aVar)))
            val second = Substitution.unifier(mapOf(aVar to xAtom))

            val firstComposedSecondExpected =
                Substitution.unifier(
                    mapOf(
                        bVar to Struct.of("f", xAtom),
                        aVar to xAtom,
                    ),
                )

            val secondComposedFirstExpected =
                Substitution.unifier(
                    mapOf(
                        aVar to xAtom,
                        bVar to Struct.of("f", aVar),
                    ),
                )

            assertEquals(firstComposedSecondExpected, first + second)
            assertEquals(secondComposedFirstExpected, second + first)
        }

        test("minus-variables-iterable-removes-correct-bindings") {
            val correct = aVarToXAtomSubstitution
            val toBeTested = (aVarToXAtomSubstitution + bVarToXAtomSubstitution) - listOf(bVar)

            assertEquals(correct, toBeTested)
        }

        test("minus-variable-iterable-with-no-common-variable-does-nothing") {
            assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution - listOf(bVar))
        }

        test("minus-other-substitution-removes-correct-bindings") {
            val correct = aVarToXAtomSubstitution
            val toBeTested = (aVarToXAtomSubstitution + bVarToXAtomSubstitution) - bVarToXAtomSubstitution

            assertEquals(correct, toBeTested)
        }

        test("minus-other-substitution-with-no-common-variables-does-nothing") {
            assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution - bVarToXAtomSubstitution)
        }

        test("minus-failed-substitution-does-nothing") {
            assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution - Substitution.failed())
        }

        test("filter-map-entry-returns-only-correct-bindings") {
            assertEquals(
                aVarToXAtomSubstitution,
                (aVarToXAtomSubstitution + bVarToXAtomSubstitution).filter { (`var`, _) -> `var` == aVar },
            )
            assertEquals(Substitution.empty(), bVarToXAtomSubstitution.filter { (`var`, _) -> `var` == aVar })
        }

        test("filter-predicate-returns-only-correct-bindings") {
            assertEquals(
                aVarToXAtomSubstitution,
                (aVarToXAtomSubstitution + bVarToXAtomSubstitution).filter { `var`, _ -> `var` == aVar },
            )
            assertEquals(Substitution.empty(), bVarToXAtomSubstitution.filter { `var`, _ -> `var` == aVar })
        }

        test("get-returns-last-bound-term-in-the-variable-chain") {
            val myAtom = Atom.of("hello")
            val xVar = Var.of("X")
            val yVar = Var.of("Y")

            val toBeTested = Substitution.of(xVar to yVar) + Substitution.of(yVar to myAtom)

            assertEquals(myAtom, toBeTested[xVar])
        }
    })
