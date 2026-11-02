package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.SubstitutionUtils

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
            unifier.getByName("A").shouldBeInstanceOf<Integer>()
            unifier.getByName("B").shouldBeInstanceOf<Atom>()
            unifier.getByName("C").shouldBeNull()
        }

        test("unifier-constructor-returns-empty-unifier-if-identity-mappings-detected") {
            Substitution.unifier(mapOf(aVar to aVar)) shouldBe Substitution.unifier(emptyMap())
        }

        test("unifier-constructor-returns-empty-unifier-if-circular-identity-mappings-detected") {
            Substitution.unifier(mapOf(aVar to bVar, bVar to aVar)) shouldBe Substitution.unifier(emptyMap())
        }

        test("unifier-constructor-returns-trimmed-variable-chain") {
            val correct = Substitution.unifier(mapOf(aVar to xAtom, bVar to xAtom))
            val toBeTested = Substitution.unifier(mapOf(aVar to bVar, bVar to xAtom))
            toBeTested shouldBe correct
        }

        withTests(nameFn = { "is-success-is-true/${it.id}" }, substitutionCases) { case ->
            case.substitution.isSuccess.shouldBeTrue()
        }

        withTests(nameFn = { "is-failed-is-false/${it.id}" }, substitutionCases) { case ->
            case.substitution.isFailed.shouldBeFalse()
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
            actual shouldNotBe expected
            assertStructurallyEquals(expected, actual)
        }

        withTests(
            nameFn = { "apply-to-should-not-substitute-not-corresponding-variable/${it.id}" },
            SubstitutionUtils.termsWithCases,
        ) { case ->
            assertEqualities(case.build(aVar), bVarToXAtomSubstitution.applyTo(case.build(aVar)))
        }

        test("equals-works-as-expected") {
            Substitution.unifier(mapOf(aVar to xAtom)) shouldBe Substitution.unifier(mapOf(aVar to xAtom))
            Substitution.unifier(mapOf(Var.of("A") to xAtom)) shouldNotBe
                Substitution.unifier(mapOf(Var.of("A") to xAtom))
            bVarToXAtomSubstitution shouldNotBe aVarToXAtomSubstitution
        }

        test("plus-other-substitution-puts-substitutions-together") {
            val correct =
                Substitution.of((aVarToXAtomSubstitution.entries + bVarToXAtomSubstitution.entries).map { it.toPair() })
            val toBeTested = aVarToXAtomSubstitution + bVarToXAtomSubstitution

            toBeTested shouldBe correct
        }

        test("plus-other-substitution-replaces-present-variables") {
            val myAtom = Atom.of("hello")
            val xVar = Var.of("X")
            val yVar = Var.of("Y")

            val correct = Substitution.of(xVar to myAtom, yVar to myAtom)
            val toBeTested = Substitution.of(xVar to yVar) + Substitution.of(yVar to myAtom)

            toBeTested shouldBe correct
        }

        test("plus-other-substitution-is-idempotent-if-same-substitution") {
            (aVarToXAtomSubstitution + aVarToXAtomSubstitution) shouldBe aVarToXAtomSubstitution
        }

        test("plus-other-substitution-contradicting-base-one-returns-failed-substitution") {
            val toBeTested =
                listOf(
                    Substitution.of(bVar to Truth.TRUE) + bVarToXAtomSubstitution,
                    bVarToXAtomSubstitution + Substitution.of(bVar to Truth.TRUE),
                )

            toBeTested.forEach { it shouldBe Substitution.failed() }
        }

        withTests(nameFn = { "plus-failed-substitution-return-failed-substitution/${it.id}" }, substitutionCases) {
            val substitution = it.substitution
            (substitution + Substitution.failed()) shouldBe Substitution.failed()
            (Substitution.failed() + substitution) shouldBe Substitution.failed()
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

            (first + second) shouldBe firstComposedSecondExpected
            (second + first) shouldBe secondComposedFirstExpected
        }

        test("minus-variables-iterable-removes-correct-bindings") {
            val correct = aVarToXAtomSubstitution
            val toBeTested = (aVarToXAtomSubstitution + bVarToXAtomSubstitution) - listOf(bVar)

            toBeTested shouldBe correct
        }

        test("minus-variable-iterable-with-no-common-variable-does-nothing") {
            (aVarToXAtomSubstitution - listOf(bVar)) shouldBe aVarToXAtomSubstitution
        }

        test("minus-other-substitution-removes-correct-bindings") {
            val correct = aVarToXAtomSubstitution
            val toBeTested = (aVarToXAtomSubstitution + bVarToXAtomSubstitution) - bVarToXAtomSubstitution

            toBeTested shouldBe correct
        }

        test("minus-other-substitution-with-no-common-variables-does-nothing") {
            (aVarToXAtomSubstitution - bVarToXAtomSubstitution) shouldBe aVarToXAtomSubstitution
        }

        test("minus-failed-substitution-does-nothing") {
            (aVarToXAtomSubstitution - Substitution.failed()) shouldBe aVarToXAtomSubstitution
        }

        test("filter-map-entry-returns-only-correct-bindings") {
            ((aVarToXAtomSubstitution + bVarToXAtomSubstitution).filter { (`var`, _) -> `var` == aVar }) shouldBe
                aVarToXAtomSubstitution
            (bVarToXAtomSubstitution.filter { (`var`, _) -> `var` == aVar }) shouldBe Substitution.empty()
        }

        test("filter-predicate-returns-only-correct-bindings") {
            ((aVarToXAtomSubstitution + bVarToXAtomSubstitution).filter { `var`, _ -> `var` == aVar }) shouldBe
                aVarToXAtomSubstitution
            (bVarToXAtomSubstitution.filter { `var`, _ -> `var` == aVar }) shouldBe Substitution.empty()
        }

        test("get-returns-last-bound-term-in-the-variable-chain") {
            val myAtom = Atom.of("hello")
            val xVar = Var.of("X")
            val yVar = Var.of("Y")

            val toBeTested = Substitution.of(xVar to yVar) + Substitution.of(yVar to myAtom)

            toBeTested[xVar] shouldBe myAtom
        }
    })
