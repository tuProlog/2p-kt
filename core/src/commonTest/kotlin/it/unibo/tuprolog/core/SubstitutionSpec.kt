package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName

/**
 * Test class for [Substitution] companion object
 *
 * @author Enrico
 */
class SubstitutionSpec :
    FunSpec({
        val aVar = Var.of("A")

        val bVar = Var.of("B")

        test("failed-should-return-the-failed-substitution-instance") {
            Substitution.failed() shouldBe Substitution.failed()
            Substitution.failed() shouldBeSameInstanceAs Substitution.failed()
        }

        test("failed-substitution-is-failed") {
            Substitution.failed().isFailed.shouldBeTrue()
        }

        test("failed-substitution-is-not-success") {
            Substitution.failed().isSuccess.shouldBeFalse()
        }

        test("empty-returns-an-empty-substitution") {
            Substitution.empty().isEmpty().shouldBeTrue()
        }

        test("empty-substitution-is-success") {
            Substitution.empty().isSuccess.shouldBeTrue()
        }

        test("empty-substitution-is-not-failed") {
            Substitution.empty().isFailed.shouldBeFalse()
        }

        withTests(nameFn = { "as-unifier/${it.id}" }, SubstitutionUtils.mixedSubstitutionCases) { case ->
            case.map.asUnifier() shouldBe Substitution.of(case.map)
        }

        test("as-unifier-results-in-empty-substitution-if-identity-mappings") {
            mapOf(aVar to aVar).asUnifier() shouldBe Substitution.empty()
        }

        test("as-unifier-results-in-empty-substitution-if-indirect-identity-mappings") {
            mapOf(aVar to bVar, bVar to aVar).asUnifier() shouldBe Substitution.empty()
        }

        val singleMappingCases = SubstitutionUtils.mixedSubstitutionCases.filter { it.map.size == 1 }

        withTests(nameFn = { "of-variable-and-term/${it.id}" }, singleMappingCases) { case ->
            val (variable, withTerm) = case.pairs.single()
            Substitution.of(variable, withTerm) shouldBe Substitution.of(case.map)
        }

        withTests(nameFn = { "of-string-variable-and-term/${it.id}" }, singleMappingCases) { case ->
            val (variable, withTerm) = case.pairs.single()
            val expected = Substitution.of(case.map)
            val actual = Substitution.of(variable.name, withTerm)
            actual.size shouldBe expected.size
            assertDifferentVariableExceptForName(expected.keys.single(), actual.keys.single())
            actual.values.toList() shouldBe expected.values.toList()
        }

        withTests(nameFn = { "of-variable-term-pairs/${it.id}" }, SubstitutionUtils.mixedSubstitutionCases) { case ->
            Substitution.of(case.pairs.first(), *case.pairs.dropFirst().toTypedArray()) shouldBe
                Substitution.of(case.map)
        }

        withTests(
            nameFn = { "of-variable-term-pairs-doesnt-complain-if-exact-duplicates/${it.id}" },
            SubstitutionUtils.duplicatedPairCases,
        ) { case ->
            Substitution.of(case.pairs.first(), *case.pairs.dropFirst().toTypedArray()) shouldBe
                Substitution.of(case.pairs.toMap())
        }

        withTests(
            nameFn = { "of-variable-term-pairs-result-in-failed-substitution-if-contradicting/${it.id}" },
            SubstitutionUtils.contradictingPairCases,
        ) { case ->
            Substitution.of(case.pairs.first(), *case.pairs.dropFirst().toTypedArray()) shouldBe Substitution.failed()
        }

        withTests(
            nameFn = { "of-iterable-variable-term-pairs/${it.id}" },
            SubstitutionUtils.mixedSubstitutionCases,
        ) { case ->
            Substitution.of(case.pairs) shouldBe Substitution.of(case.map)
        }

        withTests(
            nameFn = { "of-iterable-variable-term-pairs-doesnt-complain-if-exact-duplicates/${it.id}" },
            SubstitutionUtils.duplicatedPairCases,
        ) { case ->
            Substitution.of(case.pairs) shouldBe Substitution.unifier(case.pairs.toMap())
        }

        withTests(
            nameFn = { "of-iterable-variable-term-pairs-result-in-failed-substitution-if-contradicting/${it.id}" },
            SubstitutionUtils.contradictingPairCases,
        ) { case ->
            Substitution.of(case.pairs) shouldBe Substitution.failed()
        }

        test("of-empty-iterable") {
            Substitution.of(emptyList()) shouldBe Substitution.empty()
        }

        test("of-substitutions") {
            val correctInstances = SubstitutionUtils.mixedSubstitutionCases.map { Substitution.of(it.map) }
            val correct =
                Substitution.unifier(
                    SubstitutionUtils.mixedSubstitutionCases.map { it.map }.reduce {
                        map1,
                        map2,
                        ->
                        map1 + map2
                    },
                )
            val toBeTested = Substitution.of(correctInstances.first(), *correctInstances.dropFirst().toTypedArray())

            toBeTested shouldBe correct
        }

        test("of-substitutions-with-always-same-substitutions-returns-them-not-duplicated") {
            Scope.of("A", "B") {
                val correct =
                    Substitution.unifier(
                        mapOf(
                            varOf("A") to atomOf("a"),
                            varOf("B") to atomOf("b"),
                        ),
                    )

                val toBeTested =
                    Substitution.of(
                        Substitution.unifier(mapOf(varOf("B") to atomOf("b"))),
                        Substitution.unifier(mapOf(varOf("A") to atomOf("a"))),
                        Substitution.unifier(mapOf(varOf("B") to atomOf("b"))),
                        Substitution.unifier(mapOf(varOf("A") to atomOf("a"))),
                        Substitution.unifier(mapOf(varOf("B") to atomOf("b"))),
                    )

                toBeTested shouldBe correct
            }
        }

        test("of-substitutions-with-contradicting-ones-returns-failed") {
            Scope.of("A", "B") {
                val toBeTested =
                    Substitution.of(
                        Substitution.unifier(mapOf(varOf("B") to atomOf("f"))),
                        Substitution.unifier(mapOf(varOf("B") to varOf("A"))),
                        Substitution.unifier(mapOf(varOf("A") to atomOf("b"))),
                        Substitution.unifier(mapOf(varOf("A") to atomOf("a"))),
                        Substitution.unifier(mapOf(varOf("B") to atomOf("b"))),
                    )

                toBeTested shouldBe Substitution.failed()
            }
        }

        test("of-substitutions-complies-with-composition-defined-in-prolog-standard") {
            val xAtom = Atom.of("x")

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

            Substitution.of(first, second) shouldBe firstComposedSecondExpected
            Substitution.of(second, first) shouldBe secondComposedFirstExpected
        }

        test("of-implementations-return-empty-substitution-if-identity-passed-in") {
            Scope.empty {
                val identityPairA = varOf("A") to varOf("A")
                val identityPairB = varOf("B") to varOf("B")
                val toBeTested =
                    listOf(
                        Substitution.of(identityPairA.first, identityPairA.second),
                        Substitution.of(identityPairA, identityPairB),
                        Substitution.of(listOf(identityPairA, identityPairB)),
                        Substitution.of(Substitution.of(identityPairA), Substitution.of(identityPairB)),
                    )

                toBeTested.forEach { it shouldBe Substitution.empty() }
            }
        }

        test("substitution-merge-fails-in-assignment-chains") {
            Scope.empty {
                val base = unifierOf("X" to varOf("Y"), "Y" to varOf("Z"))
                val a = atomOf("a")

                val assignment1 = unifierOf("X" to a)
                val assignment2 = unifierOf("Y" to a)
                val assignment3 = unifierOf("Z" to a)

                (base + assignment1) shouldBe Substitution.failed()
                (base + assignment2) shouldBe Substitution.failed()
                (base + assignment3) shouldBe unifierOf("X" to a, "Y" to a, "Z" to a)
            }
        }

        test("when-is/dispatches-a-unifier") {
            Substitution.empty().whenIs(unifier = { "unifier" }, fail = { "fail" }) shouldBe "unifier"
        }

        test("when-is/dispatches-a-failure") {
            Substitution.failed().whenIs(unifier = { "unifier" }, fail = { "fail" }) shouldBe "fail"
        }

        test("when-is/falls-back-to-otherwise") {
            Substitution.failed().whenIs(unifier = { "unifier" }, otherwise = { "other" }) shouldBe "other"
        }

        test("when-is/without-a-handler-throws") {
            shouldThrow<IllegalStateException> { Substitution.empty().whenIs<Unit>(fail = {}) }
        }

        test("cast-to-unifier/rejects-a-failure") {
            shouldThrow<ClassCastException> { Substitution.failed().castToUnifier() }
        }

        test("cast-to-fail/rejects-a-unifier") {
            shouldThrow<ClassCastException> { Substitution.empty().castToFail() }
        }

        test("fail/has-no-original-variables") {
            Substitution.failed().getOriginal(Var.of("X")).shouldBeNull()
        }

        test("unifier/of-a-variable-name") {
            Substitution.unifier("X", Atom.of("a")).getByName("X") shouldBe Atom.of("a")
        }

        test("unifier/of-a-sequence") {
            val x = Var.of("X")
            Substitution.unifier(sequenceOf(x to Atom.of("a")))[x] shouldBe Atom.of("a")
        }

        test("minus/several-variables") {
            val (x, y, z) = listOf(Var.of("X"), Var.of("Y"), Var.of("Z"))
            val a = Atom.of("a")
            Substitution.of(x to a, y to a, z to a).minus(x, y) shouldBe Substitution.of(z, a)
            Substitution
                .failed()
                .minus(x, y)
                .isFailed
                .shouldBeTrue()
        }
    })
