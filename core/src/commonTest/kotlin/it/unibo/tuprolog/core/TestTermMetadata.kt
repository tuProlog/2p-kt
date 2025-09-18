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

class TestTermMetadata {
    @Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
    private val X = Var.of("X")

    private val terms =
        listOf(
            X,
            Var.anonymous(),
            Atom.of("atom"),
            Integer.ONE,
            Real.ONE_TENTH,
            EmptyList(),
            EmptyBlock(),
            Cons.of(Real.ONE_HALF, X),
            Struct.of("g", X),
            List.of(X),
            Block.of(X),
            Tuple.of(X, X),
            Fact.of(Struct.of("f", X)),
            Fact.of(List.of(X)),
            Fact.of(Block.of(X)),
            Fact.of(Tuple.of(X, X)),
            Directive.of(Struct.of("h", X), Tuple.of(X, X)),
            Rule.of(Struct.of("h", X), Tuple.of(X, X)),
        )

    private data class Metadata<T>(
        val value: T,
    )

    private val someKey = "some_key1"
    private val someOtherKey = "some_key2"
    private val someValue1 = Metadata(0)
    private val someValue2 = Metadata(1)
    private val someOtherValue = Metadata(2)

    @Test
    fun tagsStickToTerms() {
        for (term in terms) {
            assertNull(term.getTag<Metadata<Int>>(someKey))
            assertFalse { term.containsTag(someKey) }
            val tagged = term.setTag(someKey, someValue1)
            assertFalse { term.containsTag(someKey) }
            assertNull(term.getTag<Metadata<Int>>(someKey))
            assertTrue { tagged.containsTag(someKey) }
            assertEquals(someValue1, tagged.getTag<Metadata<Int>>(someKey))
            assertEquals(emptyMap(), term.tags)
            assertEquals(mapOf(someKey to someValue1), tagged.tags)
        }
    }

    @Test
    fun tagsCanBeReplaced() {
        for (term in terms.map { it.setTag(someKey, someValue1) }) {
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = term.replaceTags(tags)
            assertEquals(mapOf(someKey to someValue1), term.tags)
            assertEquals(tags, replaced.tags)
        }
    }

    @Test
    fun addingTagsPreservesEquality() {
        for ((nonTagged, tagged) in terms.zip(terms.map { it.addTag(someKey, someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun summingTagsPreservesEquality() {
        for ((nonTagged, tagged) in terms.zip(terms.map { it + (someKey to someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun settingTagsPreservesEquality() {
        for ((nonTagged, tagged) in terms.zip(terms.map { it.setTag(someKey, someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun settingMultipleTagsPreservesEquality() {
        val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for ((nonTagged, tagged) in terms.zip(taggedTerms)) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun clearingTagsPreservesEquality() {
        val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for ((tagged, cleared) in taggedTerms.zip(taggedTerms.map { it.clearTags() })) {
            assertEquals(tagged, cleared)
            assertEquals(tagged.hashCode(), cleared.hashCode())
            assertTrue { cleared.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun addingAnExistingTagReplacesThatTag() {
        val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedTerms.map { it.addTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Int>>(someKey))
        }
    }

    @Test
    fun settingAnExistingTagReplacesThatTag() {
        val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedTerms.map { it.setTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Int>>(someKey))
        }
    }

    private fun alterationPreservesTags(alter: Term.() -> Term) {
        for (term in terms) {
            assertEquals(term.tags, term.alter().tags)
        }
    }

    @Test
    fun freshCopyPreservesTags() {
        alterationPreservesTags { freshCopy() }
    }

    @Test
    fun freshCopyWithScopePreservesTags() {
        alterationPreservesTags { freshCopy(Scope.Companion.of("Y")) }
    }

    @Test
    fun applyPreservesTags() {
        alterationPreservesTags { apply(Substitution.unifier(X, Integer.ONE)) }
    }

    @Test
    fun getPreservesTags() {
        alterationPreservesTags { apply(Substitution.unifier(X, Real.ONE)) }
    }
}
