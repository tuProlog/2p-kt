package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
            assertEquals(Substitution.failed(), Substitution.failed())
            assertSame(Substitution.failed(), Substitution.failed())
        }

        test("failed-substitution-is-failed") {
            assertTrue(Substitution.failed().isFailed)
        }

        test("failed-substitution-is-not-success") {
            assertFalse(Substitution.failed().isSuccess)
        }

        test("empty-returns-an-empty-substitution") {
            assertTrue(Substitution.empty().isEmpty())
        }

        test("empty-substitution-is-success") {
            assertTrue(Substitution.empty().isSuccess)
        }

        test("empty-substitution-is-not-failed") {
            assertFalse(Substitution.empty().isFailed)
        }

        withTests(nameFn = { "as-unifier/${it.id}" }, SubstitutionUtils.mixedSubstitutionCases) { case ->
            assertEquals(Substitution.of(case.map), case.map.asUnifier())
        }

        test("as-unifier-results-in-empty-substitution-if-identity-mappings") {
            assertEquals(Substitution.empty(), mapOf(aVar to aVar).asUnifier())
        }

        test("as-unifier-results-in-empty-substitution-if-indirect-identity-mappings") {
            assertEquals(Substitution.empty(), mapOf(aVar to bVar, bVar to aVar).asUnifier())
        }

        val singleMappingCases = SubstitutionUtils.mixedSubstitutionCases.filter { it.map.size == 1 }

        withTests(nameFn = { "of-variable-and-term/${it.id}" }, singleMappingCases) { case ->
            val (variable, withTerm) = case.pairs.single()
            assertEquals(Substitution.of(case.map), Substitution.of(variable, withTerm))
        }

        withTests(nameFn = { "of-string-variable-and-term/${it.id}" }, singleMappingCases) { case ->
            val (variable, withTerm) = case.pairs.single()
            val expected = Substitution.of(case.map)
            val actual = Substitution.of(variable.name, withTerm)
            assertEquals(expected.size, actual.size)
            assertDifferentVariableExceptForName(expected.keys.single(), actual.keys.single())
            assertEquals(expected.values.toList(), actual.values.toList())
        }

        withTests(nameFn = { "of-variable-term-pairs/${it.id}" }, SubstitutionUtils.mixedSubstitutionCases) { case ->
            assertEquals(
                Substitution.of(case.map),
                Substitution.of(case.pairs.first(), *case.pairs.dropFirst().toTypedArray()),
            )
        }

        withTests(
            nameFn = { "of-variable-term-pairs-doesnt-complain-if-exact-duplicates/${it.id}" },
            SubstitutionUtils.duplicatedPairCases,
        ) { case ->
            assertEquals(
                Substitution.of(case.pairs.toMap()),
                Substitution.of(case.pairs.first(), *case.pairs.dropFirst().toTypedArray()),
            )
        }

        withTests(
            nameFn = { "of-variable-term-pairs-result-in-failed-substitution-if-contradicting/${it.id}" },
            SubstitutionUtils.contradictingPairCases,
        ) { case ->
            assertEquals(
                Substitution.failed(),
                Substitution.of(case.pairs.first(), *case.pairs.dropFirst().toTypedArray()),
            )
        }

        withTests(
            nameFn = { "of-iterable-variable-term-pairs/${it.id}" },
            SubstitutionUtils.mixedSubstitutionCases,
        ) { case ->
            assertEquals(Substitution.of(case.map), Substitution.of(case.pairs))
        }

        withTests(
            nameFn = { "of-iterable-variable-term-pairs-doesnt-complain-if-exact-duplicates/${it.id}" },
            SubstitutionUtils.duplicatedPairCases,
        ) { case ->
            assertEquals(Substitution.unifier(case.pairs.toMap()), Substitution.of(case.pairs))
        }

        withTests(
            nameFn = { "of-iterable-variable-term-pairs-result-in-failed-substitution-if-contradicting/${it.id}" },
            SubstitutionUtils.contradictingPairCases,
        ) { case ->
            assertEquals(Substitution.failed(), Substitution.of(case.pairs))
        }

        test("of-empty-iterable") {
            assertEquals(Substitution.empty(), Substitution.of(emptyList()))
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

            assertEquals(correct, toBeTested)
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

                assertEquals(correct, toBeTested)
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

                assertEquals(Substitution.failed(), toBeTested)
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

            assertEquals(firstComposedSecondExpected, Substitution.of(first, second))
            assertEquals(secondComposedFirstExpected, Substitution.of(second, first))
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

                toBeTested.forEach { assertEquals(Substitution.empty(), it) }
            }
        }

        test("substitution-merge-fails-in-assignment-chains") {
            Scope.empty {
                val base = unifierOf("X" to varOf("Y"), "Y" to varOf("Z"))
                val a = atomOf("a")

                val assignment1 = unifierOf("X" to a)
                val assignment2 = unifierOf("Y" to a)
                val assignment3 = unifierOf("Z" to a)

                assertEquals(Substitution.failed(), base + assignment1)
                assertEquals(Substitution.failed(), base + assignment2)
                assertEquals(unifierOf("X" to a, "Y" to a, "Z" to a), base + assignment3)
            }
        }

        test("when-is/dispatches-a-unifier") {
            assertEquals("unifier", Substitution.empty().whenIs(unifier = { "unifier" }, fail = { "fail" }))
        }

        test("when-is/dispatches-a-failure") {
            assertEquals("fail", Substitution.failed().whenIs(unifier = { "unifier" }, fail = { "fail" }))
        }

        test("when-is/falls-back-to-otherwise") {
            assertEquals("other", Substitution.failed().whenIs(unifier = { "unifier" }, otherwise = { "other" }))
        }

        test("when-is/without-a-handler-throws") {
            assertFailsWith<IllegalStateException> { Substitution.empty().whenIs<Unit>(fail = {}) }
        }

        test("cast-to-unifier/rejects-a-failure") {
            assertFailsWith<ClassCastException> { Substitution.failed().castToUnifier() }
        }

        test("cast-to-fail/rejects-a-unifier") {
            assertFailsWith<ClassCastException> { Substitution.empty().castToFail() }
        }

        test("fail/has-no-original-variables") {
            assertNull(Substitution.failed().getOriginal(Var.of("X")))
        }

        test("unifier/of-a-variable-name") {
            assertEquals(Atom.of("a"), Substitution.unifier("X", Atom.of("a")).getByName("X"))
        }

        test("unifier/of-a-sequence") {
            val x = Var.of("X")
            assertEquals(Atom.of("a"), Substitution.unifier(sequenceOf(x to Atom.of("a")))[x])
        }

        test("minus/several-variables") {
            val (x, y, z) = listOf(Var.of("X"), Var.of("Y"), Var.of("Z"))
            val a = Atom.of("a")
            assertEquals(Substitution.of(z, a), Substitution.of(x to a, y to a, z to a).minus(x, y))
            assertTrue(Substitution.failed().minus(x, y).isFailed)
        }
    })
