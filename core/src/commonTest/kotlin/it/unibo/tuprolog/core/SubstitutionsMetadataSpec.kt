@file:Suppress("PrivatePropertyName", "ktlint:standard:property-naming")

package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SubstitutionsMetadataSpec :
    FunSpec({
        val X = Var.of("X")

        val Y = Var.of("Y")

        val Z = Var.of("Z")

        val substitutions =
            listOf(
                Substitution.failed(),
                Substitution.empty(),
                Substitution.unifier(Y, X),
                Substitution.unifier(Y, Var.anonymous()),
                Substitution.unifier(Y, Atom.of("atom")),
                Substitution.unifier(Y, Integer.ONE),
                Substitution.unifier(Y, Real.ONE_TENTH),
                Substitution.unifier(Y, EmptyList()),
                Substitution.unifier(Y, EmptyBlock()),
                Substitution.unifier(Y, Cons.of(Real.ONE_HALF, X)),
                Substitution.unifier(Y, Struct.of("g", X)),
                Substitution.unifier(Y, List.of(X)),
                Substitution.unifier(Y, Block.of(X)),
                Substitution.unifier(Y, Tuple.of(X, X)),
                Substitution.unifier(Y, Fact.of(Struct.of("f", X))),
                Substitution.unifier(Y, Fact.of(List.of(X))),
                Substitution.unifier(Y, Fact.of(Block.of(X))),
                Substitution.unifier(Y, Fact.of(Tuple.of(X, X))),
                Substitution.unifier(Y, Directive.of(Struct.of("h", X), Tuple.of(X, X))),
                Substitution.unifier(Y, Rule.of(Struct.of("h", X), Tuple.of(X, X))),
                Substitution.of(Z, X),
                Substitution.of(Z, Var.anonymous()),
                Substitution.of(Z, Atom.of("atom")),
                Substitution.of(Z, Integer.ONE),
                Substitution.of(Z, Real.ONE_TENTH),
                Substitution.of(Z, EmptyList()),
                Substitution.of(Z, EmptyBlock()),
                Substitution.of(Z, Cons.of(Real.ONE_HALF, X)),
                Substitution.of(Z, Struct.of("g", X)),
                Substitution.of(Z, List.of(X)),
                Substitution.of(Z, Block.of(X)),
                Substitution.of(Z, Tuple.of(X, X)),
                Substitution.of(Z, Fact.of(Struct.of("f", X))),
                Substitution.of(Z, Fact.of(List.of(X))),
                Substitution.of(Z, Fact.of(Block.of(X))),
                Substitution.of(Z, Fact.of(Tuple.of(X, X))),
                Substitution.of(Z, Directive.of(Struct.of("h", X), Tuple.of(X, X))),
                Substitution.of(Z, Rule.of(Struct.of("h", X), Tuple.of(X, X))),
            )

        val someKey = "some_key1"

        val someOtherKey = "some_key2"

        val yetAnotherKey = "some_key3"

        val someValue1 = SubstitutionTag(0L)

        val someValue2 = SubstitutionTag(1L)

        val someOtherValue = SubstitutionTag(2L)

        fun alteringPreservesTags(alter: Substitution.() -> Substitution) {
            val tags = mapOf(someKey to someValue1, someOtherKey to someValue2)
            for (substitution in substitutions.map { it.setTags(tags) }) {
                assertEquals(tags, substitution.tags)
                assertEquals(substitution.tags, substitution.alter().tags)
            }
        }

        fun summingMergesTags(
            other: Substitution,
            sum: Substitution.(Substitution) -> Substitution,
        ) {
            val tags = mapOf(someKey to someValue1, someOtherKey to someValue2)
            for (substitution in substitutions.map { it.setTags(tags) }) {
                assertEquals(tags, substitution.tags)
                assertEquals(tags + other.tags, substitution.sum(other).tags)
            }
        }

        test("tags-stick-to-substitutions") {
            for (substitution in substitutions) {
                assertNull(substitution.getTag<SubstitutionTag<Long>>(someKey))
                assertFalse { substitution.containsTag(someKey) }
                val tagged = substitution.setTag(someKey, someValue1)
                assertFalse { substitution.containsTag(someKey) }
                assertNull(substitution.getTag<SubstitutionTag<Long>>(someKey))
                assertTrue { tagged.containsTag(someKey) }
                assertEquals(someValue1, tagged.getTag<SubstitutionTag<Long>>(someKey))
                assertEquals(emptyMap(), substitution.tags)
                assertEquals(mapOf(someKey to someValue1), tagged.tags)
            }
        }

        test("tags-can-be-replaced") {
            for (substitution in substitutions.map { it.setTag(someKey, someValue1) }) {
                val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
                val replaced = substitution.replaceTags(tags)
                assertEquals(mapOf(someKey to someValue1), substitution.tags)
                assertEquals(tags, replaced.tags)
            }
        }

        test("adding-tags-preserves-equality") {
            for ((nonTagged, tagged) in substitutions.zip(substitutions.map { it.addTag(someKey, someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("summing-tags-preserves-equality") {
            for ((nonTagged, tagged) in substitutions.zip(substitutions.map { it + (someKey to someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("setting-tags-preserves-equality") {
            for ((nonTagged, tagged) in substitutions.zip(substitutions.map { it.setTag(someKey, someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("setting-multiple-tags-preserves-equality") {
            val taggedSubstitutions =
                substitutions.map {
                    it.setTags(
                        someKey to someValue1,
                        someOtherKey to someOtherValue,
                    )
                }
            for ((nonTagged, tagged) in substitutions.zip(taggedSubstitutions)) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("clearing-tags-preserves-equality") {
            val taggedSubstitutions =
                substitutions.map {
                    it.setTags(
                        someKey to someValue1,
                        someOtherKey to someOtherValue,
                    )
                }
            for ((tagged, cleared) in taggedSubstitutions.zip(taggedSubstitutions.map { it.clearTags() })) {
                assertEquals(tagged, cleared)
                assertEquals(tagged.hashCode(), cleared.hashCode())
                assertTrue { cleared.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("adding-an-existing-tag-replaces-that-tag") {
            val taggedSubstitutions =
                substitutions.map {
                    it.setTags(
                        someKey to someValue1,
                        someOtherKey to someOtherValue,
                    )
                }
            for (replaced in taggedSubstitutions.map { it.addTag(someKey, someValue2) }) {
                assertEquals(someValue2, replaced.getTag<SubstitutionTag<Long>>(someKey))
            }
        }

        test("setting-an-existing-tag-replaces-that-tag") {
            val taggedSubstitutions =
                substitutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for (replaced in taggedSubstitutions.map { it.setTag(someKey, someValue2) }) {
                assertEquals(someValue2, replaced.getTag<SubstitutionTag<Long>>(someKey))
            }
        }

        test("minus-iterable-preserves-tags") {
            alteringPreservesTags { this - listOf(X, Y, Z) }
        }

        test("minus-preserves-tags") {
            alteringPreservesTags { this - X - Y - Z }
        }

        test("filter-with-function-preserves-tags") {
            alteringPreservesTags { filter { (k, _) -> k in setOf(X, Y, Z) } }
        }

        test("filter-preserves-tags") {
            alteringPreservesTags { filter(setOf(X, Y, Z)) }
        }

        test("plus-preserves-tags") {
            summingMergesTags(Substitution.empty().setTag(yetAnotherKey, someOtherValue)) { plus(it) }
        }

        test("plus-with-function-preserves-tags") {
            summingMergesTags(Substitution.of(X to EmptyBlock()).setTag(yetAnotherKey, someOtherValue)) {
                plus(it) { x, y -> x + y }
            }
        }
    })

private data class SubstitutionTag<T>(
    val value: T,
)
