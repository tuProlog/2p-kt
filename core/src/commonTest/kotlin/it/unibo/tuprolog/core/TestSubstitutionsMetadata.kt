package it.unibo.tuprolog.core

import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
class TestSubstitutionsMetadata {
    private val X = Var.of("X")

    private val Y = Var.of("Y")

    private val Z = Var.of("Z")

    private val substitutions =
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

    private data class Metadata<T>(
        val value: T,
    )

    private val someKey = "some_key1"
    private val someOtherKey = "some_key2"
    private val yetAnotherKey = "some_key3"
    private val someValue1 = Metadata(0L)
    private val someValue2 = Metadata(1L)
    private val someOtherValue = Metadata(2L)

    @Test
    fun tagsStickToSubstitutions() {
        for (substitution in substitutions) {
            assertNull(substitution.getTag<Metadata<Long>>(someKey))
            assertFalse { substitution.containsTag(someKey) }
            val tagged = substitution.setTag(someKey, someValue1)
            assertFalse { substitution.containsTag(someKey) }
            assertNull(substitution.getTag<Metadata<Long>>(someKey))
            assertTrue { tagged.containsTag(someKey) }
            assertEquals(someValue1, tagged.getTag<Metadata<Long>>(someKey))
            assertEquals(emptyMap(), substitution.tags)
            assertEquals(mapOf(someKey to someValue1), tagged.tags)
        }
    }

    @Test
    fun tagsCanBeReplaced() {
        for (substitution in substitutions.map { it.setTag(someKey, someValue1) }) {
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = substitution.replaceTags(tags)
            assertEquals(mapOf(someKey to someValue1), substitution.tags)
            assertEquals(tags, replaced.tags)
        }
    }

    @Test
    fun addingTagsPreservesEquality() {
        for ((nonTagged, tagged) in substitutions.zip(substitutions.map { it.addTag(someKey, someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun summingTagsPreservesEquality() {
        for ((nonTagged, tagged) in substitutions.zip(substitutions.map { it + (someKey to someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun settingTagsPreservesEquality() {
        for ((nonTagged, tagged) in substitutions.zip(substitutions.map { it.setTag(someKey, someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun settingMultipleTagsPreservesEquality() {
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

    @Test
    fun clearingTagsPreservesEquality() {
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

    @Test
    fun addingAnExistingTagReplacesThatTag() {
        val taggedSubstitutions =
            substitutions.map {
                it.setTags(
                    someKey to someValue1,
                    someOtherKey to someOtherValue,
                )
            }
        for (replaced in taggedSubstitutions.map { it.addTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Long>>(someKey))
        }
    }

    @Test
    fun settingAnExistingTagReplacesThatTag() {
        val taggedSubstitutions =
            substitutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedSubstitutions.map { it.setTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Long>>(someKey))
        }
    }

    private fun alteringPreservesTags(alter: Substitution.() -> Substitution) {
        val tags = mapOf(someKey to someValue1, someOtherKey to someValue2)
        for (substitution in substitutions.map { it.setTags(tags) }) {
            assertEquals(tags, substitution.tags)
            assertEquals(substitution.tags, substitution.alter().tags)
        }
    }

    private fun summingMergesTags(
        other: Substitution,
        sum: Substitution.(Substitution) -> Substitution,
    ) {
        val tags = mapOf(someKey to someValue1, someOtherKey to someValue2)
        for (substitution in substitutions.map { it.setTags(tags) }) {
            assertEquals(tags, substitution.tags)
            assertEquals(tags + other.tags, substitution.sum(other).tags)
        }
    }

    @Test
    fun minusIterablePreservesTags() {
        alteringPreservesTags { this - listOf(X, Y, Z) }
    }

    @Test
    fun minusPreservesTags() {
        alteringPreservesTags { this - X - Y - Z }
    }

    @Test
    fun filterWithFunctionPreservesTags() {
        alteringPreservesTags { filter { (k, _) -> k in setOf(X, Y, Z) } }
    }

    @Test
    fun filterPreservesTags() {
        alteringPreservesTags { filter(setOf(X, Y, Z)) }
    }

    @Test
    fun plusPreservesTags() {
        summingMergesTags(Substitution.empty().setTag(yetAnotherKey, someOtherValue)) { plus(it) }
    }

    @Test
    fun plusWithFunctionPreservesTags() {
        summingMergesTags(Substitution.of(X to EmptyBlock()).setTag(yetAnotherKey, someOtherValue)) {
            plus(it) { x, y -> x + y }
        }
    }
}
