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

class TestSubstitutionsMetadata {

    private val X = Var.of("X")

    private val substitutions = listOf(
        Substitution.failed(),
        Substitution.empty(),
        Substitution.unifier("Y", X),
        Substitution.unifier("Y", Var.anonymous()),
        Substitution.unifier("Y", Atom.of("atom")),
        Substitution.unifier("Y", Integer.ONE),
        Substitution.unifier("Y", Real.ONE_TENTH),
        Substitution.unifier("Y", EmptyList()),
        Substitution.unifier("Y", EmptySet()),
        Substitution.unifier("Y", Cons.of(Real.ONE_HALF, X)),
        Substitution.unifier("Y", Struct.of("g", X)),
        Substitution.unifier("Y", List.of(X)),
        Substitution.unifier("Y", Set.of(X)),
        Substitution.unifier("Y", Tuple.of(X, X)),
        Substitution.unifier("Y", Fact.of(Struct.of("f", X))),
        Substitution.unifier("Y", Fact.of(List.of(X))),
        Substitution.unifier("Y", Fact.of(Set.of(X))),
        Substitution.unifier("Y", Fact.of(Tuple.of(X, X))),
        Substitution.unifier("Y", Directive.of(Struct.of("h", X), Tuple.of(X, X))),
        Substitution.unifier("Y", Rule.of(Struct.of("h", X), Tuple.of(X, X))),
        Substitution.of("Z", X),
        Substitution.of("Z", Var.anonymous()),
        Substitution.of("Z", Atom.of("atom")),
        Substitution.of("Z", Integer.ONE),
        Substitution.of("Z", Real.ONE_TENTH),
        Substitution.of("Z", EmptyList()),
        Substitution.of("Z", EmptySet()),
        Substitution.of("Z", Cons.of(Real.ONE_HALF, X)),
        Substitution.of("Z", Struct.of("g", X)),
        Substitution.of("Z", List.of(X)),
        Substitution.of("Z", Set.of(X)),
        Substitution.of("Z", Tuple.of(X, X)),
        Substitution.of("Z", Fact.of(Struct.of("f", X))),
        Substitution.of("Z", Fact.of(List.of(X))),
        Substitution.of("Z", Fact.of(Set.of(X))),
        Substitution.of("Z", Fact.of(Tuple.of(X, X))),
        Substitution.of("Z", Directive.of(Struct.of("h", X), Tuple.of(X, X))),
        Substitution.of("Z", Rule.of(Struct.of("h", X), Tuple.of(X, X))),
    )

    private data class Metadata<T>(val value: T)

    private val someKey = "some_key1"
    private val someOtherKey = "some_key2"
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
        val taggedSubstitutions = substitutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for ((nonTagged, tagged) in substitutions.zip(taggedSubstitutions)) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun clearingTagsPreservesEquality() {
        val taggedSubstitutions = substitutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for ((tagged, cleared) in taggedSubstitutions.zip(taggedSubstitutions.map { it.clearTags() })) {
            assertEquals(tagged, cleared)
            assertEquals(tagged.hashCode(), cleared.hashCode())
            assertTrue { cleared.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun addingAnExistingTagReplacesThatTag() {
        val taggedSubstitutions = substitutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedSubstitutions.map { it.addTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Long>>(someKey))
        }
    }

    @Test
    fun settingAnExistingTagReplacesThatTag() {
        val taggedSubstitutions = substitutions.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedSubstitutions.map { it.setTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Long>>(someKey))
        }
    }
}
