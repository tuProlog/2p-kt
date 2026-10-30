package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class TaggableTermCase(
    val id: String,
    val term: Term,
)

class TermMetadataSpec :
    FunSpec({
        @Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
        val X = Var.of("X")

        val termCases =
            listOf(
                TaggableTermCase("variable", X),
                TaggableTermCase("anonymous-variable", Var.anonymous()),
                TaggableTermCase("atom", Atom.of("atom")),
                TaggableTermCase("integer", Integer.ONE),
                TaggableTermCase("real", Real.ONE_TENTH),
                TaggableTermCase("empty-list", EmptyList()),
                TaggableTermCase("empty-block", EmptyBlock()),
                TaggableTermCase("cons", Cons.of(Real.ONE_HALF, X)),
                TaggableTermCase("struct", Struct.of("g", X)),
                TaggableTermCase("list", List.of(X)),
                TaggableTermCase("block", Block.of(X)),
                TaggableTermCase("tuple", Tuple.of(X, X)),
                TaggableTermCase("fact-of-struct", Fact.of(Struct.of("f", X))),
                TaggableTermCase("fact-of-list", Fact.of(List.of(X))),
                TaggableTermCase("fact-of-block", Fact.of(Block.of(X))),
                TaggableTermCase("fact-of-tuple", Fact.of(Tuple.of(X, X))),
                TaggableTermCase("directive", Directive.of(Struct.of("h", X), Tuple.of(X, X))),
                TaggableTermCase("rule", Rule.of(Struct.of("h", X), Tuple.of(X, X))),
            )

        val someKey = "some_key1"

        val someOtherKey = "some_key2"

        val someValue1 = TermTag(0)

        val someValue2 = TermTag(1)

        val someOtherValue = TermTag(2)

        fun terms(
            property: String,
            test: (Term) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, termCases) { test(it.term) }

        fun alterationPreservesTags(
            property: String,
            alter: Term.() -> Term,
        ) = terms(property) { term -> assertEquals(term.tags, term.alter().tags) }

        terms("tags-stick-to-terms") { term ->
            assertNull(term.getTag<TermTag<Int>>(someKey))
            assertFalse { term.containsTag(someKey) }
            val tagged = term.setTag(someKey, someValue1)
            assertFalse { term.containsTag(someKey) }
            assertNull(term.getTag<TermTag<Int>>(someKey))
            assertTrue { tagged.containsTag(someKey) }
            assertEquals(someValue1, tagged.getTag<TermTag<Int>>(someKey))
            assertEquals(emptyMap(), term.tags)
            assertEquals(mapOf(someKey to someValue1), tagged.tags)
        }

        terms("tags-can-be-replaced") {
            val term = it.setTag(someKey, someValue1)
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = term.replaceTags(tags)
            assertEquals(mapOf(someKey to someValue1), term.tags)
            assertEquals(tags, replaced.tags)
        }

        fun taggingPreservesEquality(
            property: String,
            tag: Term.() -> Term,
        ) = terms(property) { nonTagged ->
            val tagged = nonTagged.tag()
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }

        taggingPreservesEquality("adding-tags-preserves-equality") { addTag(someKey, someValue1) }

        taggingPreservesEquality("summing-tags-preserves-equality") { this + (someKey to someValue1) }

        taggingPreservesEquality("setting-tags-preserves-equality") { setTag(someKey, someValue1) }

        taggingPreservesEquality("setting-multiple-tags-preserves-equality") {
            setTags(someKey to someValue1, someOtherKey to someOtherValue)
        }

        terms("clearing-tags-preserves-equality") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val cleared = tagged.clearTags()
            assertEquals(tagged, cleared)
            assertEquals(tagged.hashCode(), cleared.hashCode())
            assertTrue { cleared.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }

        terms("adding-an-existing-tag-replaces-that-tag") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val replaced = tagged.addTag(someKey, someValue2)
            assertEquals(someValue2, replaced.getTag<TermTag<Int>>(someKey))
        }

        terms("setting-an-existing-tag-replaces-that-tag") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val replaced = tagged.setTag(someKey, someValue2)
            assertEquals(someValue2, replaced.getTag<TermTag<Int>>(someKey))
        }

        alterationPreservesTags("fresh-copy-preserves-tags") { freshCopy() }

        alterationPreservesTags("fresh-copy-with-scope-preserves-tags") { freshCopy(Scope.Companion.of("Y")) }

        alterationPreservesTags("apply-preserves-tags") { apply(Substitution.unifier(X, Integer.ONE)) }

        alterationPreservesTags("get-preserves-tags") { apply(Substitution.unifier(X, Real.ONE)) }
    })

private data class TermTag<T>(
    val value: T,
)
