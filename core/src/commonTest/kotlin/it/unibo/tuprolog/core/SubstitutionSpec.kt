package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

        val correctInstances by lazy { SubstitutionUtils.mixedSubstitutions.map(Substitution.Companion::of) }

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

        test("as-unifier-converts-a-map-var-term-to-substitution") {
            val toBeTested = SubstitutionUtils.mixedSubstitutions.map { it.asUnifier() }

            onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("as-unifier-results-in-empty-substitution-if-identity-mappings") {
            assertEquals(Substitution.empty(), mapOf(aVar to aVar).asUnifier())
        }

        test("as-unifier-results-in-empty-substitution-if-indirect-identity-mappings") {
            assertEquals(Substitution.empty(), mapOf(aVar to bVar, bVar to aVar).asUnifier())
        }

        test("of-variable-and-term") {
            val correct = correctInstances.filter { it.size == 1 }
            val toBeTested =
                SubstitutionUtils.mixedSubstitutionsAsPairs
                    .filter { it.size == 1 }
                    .map { it.first() }
                    .map { (variable, withTerm) -> Substitution.of(variable, withTerm) }

            onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("of-string-variable-and-term") {
            val correct = correctInstances.filter { it.size == 1 }
            val toBeTested =
                SubstitutionUtils.mixedSubstitutionsAsPairs
                    .filter { it.size == 1 }
                    .map { it.first() }
                    .map { (variable, withTerm) -> Substitution.of(variable.name, withTerm) }

            onCorrespondingItems(correct, toBeTested) { expectedMap, actualMap ->
                onCorrespondingItems(expectedMap.keys, actualMap.keys, ::assertDifferentVariableExceptForName)
                assertEquals(expectedMap.values.toList(), actualMap.values.toList())
            }
        }

        test("of-variable-term-pairs") {
            val toBeTested =
                SubstitutionUtils.mixedSubstitutionsAsPairs.map {
                    Substitution.of(it.first(), *it.dropFirst().toTypedArray())
                }

            onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("of-variable-term-pairs-doesnt-complain-if-exact-duplicates") {
            val toBeTested =
                SubstitutionUtils.duplicatedPairSubstitution.map {
                    Substitution.of(it.first(), *it.dropFirst().toTypedArray())
                }

            val correctInstances = SubstitutionUtils.duplicatedPairSubstitution.map { Substitution.of(it.toMap()) }

            onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("of-variable-term-pairs-result-in-failed-substitution-if-contradicting") {
            val toBeTested =
                SubstitutionUtils.contradictingSubstitutions.map {
                    Substitution.of(it.first(), *it.dropFirst().toTypedArray())
                }

            toBeTested.forEach { assertEquals(Substitution.failed(), it) }
        }

        test("of-iterable-variable-term-pairs") {
            val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.map { Substitution.of(it) }

            onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("of-iterable-variable-term-pairs-doesnt-complain-if-exact-duplicates") {
            val toBeTested = SubstitutionUtils.duplicatedPairSubstitution.map { Substitution.of(it) }

            val correctInstances = SubstitutionUtils.duplicatedPairSubstitution.map { Substitution.unifier(it.toMap()) }

            onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
        }

        test("of-iterable-variable-term-pairs-result-in-failed-substitution-if-contradicting") {
            val toBeTested = SubstitutionUtils.contradictingSubstitutions.map { Substitution.of(it) }

            toBeTested.forEach { assertEquals(Substitution.failed(), it) }
        }

        test("of-empty-iterable") {
            assertEquals(Substitution.empty(), Substitution.of(emptyList()))
        }

        test("of-substitutions") {
            val correct =
                Substitution.unifier(
                    SubstitutionUtils.mixedSubstitutions.reduce {
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
    })
