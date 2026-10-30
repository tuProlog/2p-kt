package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SolutionMetadataSpec :
    FunSpec({
        @Suppress("PrivatePropertyName", "ktlint:standard:property-naming")
        val X = Var.of("X")

        val someKey = "some_key1"

        val someOtherKey = "some_key2"

        val yetAnotherKey = "some_key3"

        val someValue1 = Metadata("1")

        val someValue2 = Metadata("2")

        val someOtherValue = Metadata("3")

        val queries =
            listOf<Struct>(
                Struct.of("q", X),
                Struct.of("q", Var.anonymous()),
                Atom.of("atom"),
                Struct.of("q", Integer.ONE),
                Struct.of("q", Real.ONE_TENTH),
                EmptyList(),
                EmptyBlock(),
                Cons.of(Real.ONE_HALF, X),
                Struct.of("g", X),
                List.of(X),
                Block.of(X),
                Tuple.of(X, X),
            ).map { it.setTag(yetAnotherKey, someValue2) }

        val solutions =
            queries.flatMap {
                listOf(
                    Solution.no(it),
                    Solution.halt(it, Dummy.RuntimeException),
                    Solution.yes(it),
                    Solution.yes(it, Substitution.of(X, Integer.ONE)),
                )
            }

        test("tags-stick-to-solutions") {
            for (solution in solutions) {
                assertNull(solution.getTag<Metadata<String>>(someKey))
                assertFalse { solution.containsTag(someKey) }
                val tagged = solution.setTag(someKey, someValue1)
                assertFalse { solution.containsTag(someKey) }
                assertNull(solution.getTag<Metadata<String>>(someKey))
                assertTrue { tagged.containsTag(someKey) }
                assertEquals(someValue1, tagged.getTag<Metadata<String>>(someKey))
                assertEquals(emptyMap(), solution.tags)
                assertEquals(mapOf(someKey to someValue1), tagged.tags)
            }
        }

        test("solutions-preserve-query-tags") {
            for (solution in solutions) {
                assertEquals(mapOf(yetAnotherKey to someValue2), solution.query.tags)
                if (solution is Solution.Yes) {
                    assertEquals(mapOf(yetAnotherKey to someValue2), solution.solvedQuery.tags)
                } else {
                    assertNull(solution.solvedQuery)
                }
            }
        }

        test("tags-can-be-replaced") {
            for (solution in solutions.map { it.setTag(someKey, someValue1) }) {
                val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
                val replaced = solution.replaceTags(tags)
                assertEquals(mapOf(someKey to someValue1), solution.tags)
                assertEquals(tags, replaced.tags)
            }
        }

        test("adding-tags-preserves-equality") {
            for ((nonTagged, tagged) in solutions.zip(solutions.map { it.addTag(someKey, someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("summing-tags-preserves-equality") {
            for ((nonTagged, tagged) in solutions.zip(solutions.map { it + (someKey to someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("setting-tags-preserves-equality") {
            for ((nonTagged, tagged) in solutions.zip(solutions.map { it.setTag(someKey, someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("setting-multiple-tags-preserves-equality") {
            val taggedSolutions = solutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for ((nonTagged, tagged) in solutions.zip(taggedSolutions)) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("clearing-tags-preserves-equality") {
            val taggedSolutions = solutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for ((tagged, cleared) in taggedSolutions.zip(taggedSolutions.map { it.clearTags() })) {
                assertEquals(tagged, cleared)
                assertEquals(tagged.hashCode(), cleared.hashCode())
                assertTrue { cleared.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("adding-an-existing-tag-replaces-that-tag") {
            val taggedSolutions = solutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for (replaced in taggedSolutions.map { it.addTag(someKey, someValue2) }) {
                assertEquals(someValue2, replaced.getTag<Metadata<String>>(someKey))
            }
        }

        test("setting-an-existing-tag-replaces-that-tag") {
            val taggedSubstitutions =
                solutions.map {
                    it.setTags(
                        someKey to someValue1,
                        someOtherKey to someOtherValue,
                    )
                }
            for (replaced in taggedSubstitutions.map { it.setTag(someKey, someValue2) }) {
                assertEquals(someValue2, replaced.getTag<Metadata<String>>(someKey))
            }
        }
    })

private data class Metadata<T>(
    val value: T,
)
