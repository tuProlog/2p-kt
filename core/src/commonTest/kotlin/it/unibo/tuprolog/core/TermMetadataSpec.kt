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

class TermMetadataSpec :
    FunSpec({
        @Suppress("ktlint:standard:property-naming", "PrivatePropertyName")
        val X = Var.of("X")

        val terms =
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

        val someKey = "some_key1"

        val someOtherKey = "some_key2"

        val someValue1 = TermTag(0)

        val someValue2 = TermTag(1)

        val someOtherValue = TermTag(2)

        fun alterationPreservesTags(alter: Term.() -> Term) {
            for (term in terms) {
                assertEquals(term.tags, term.alter().tags)
            }
        }

        test("tags-stick-to-terms") {
            for (term in terms) {
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
        }

        test("tags-can-be-replaced") {
            for (term in terms.map { it.setTag(someKey, someValue1) }) {
                val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
                val replaced = term.replaceTags(tags)
                assertEquals(mapOf(someKey to someValue1), term.tags)
                assertEquals(tags, replaced.tags)
            }
        }

        test("adding-tags-preserves-equality") {
            for ((nonTagged, tagged) in terms.zip(terms.map { it.addTag(someKey, someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("summing-tags-preserves-equality") {
            for ((nonTagged, tagged) in terms.zip(terms.map { it + (someKey to someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("setting-tags-preserves-equality") {
            for ((nonTagged, tagged) in terms.zip(terms.map { it.setTag(someKey, someValue1) })) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("setting-multiple-tags-preserves-equality") {
            val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for ((nonTagged, tagged) in terms.zip(taggedTerms)) {
                assertEquals(nonTagged, tagged)
                assertEquals(nonTagged.hashCode(), tagged.hashCode())
                assertTrue { nonTagged.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("clearing-tags-preserves-equality") {
            val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for ((tagged, cleared) in taggedTerms.zip(taggedTerms.map { it.clearTags() })) {
                assertEquals(tagged, cleared)
                assertEquals(tagged.hashCode(), cleared.hashCode())
                assertTrue { cleared.tags.isEmpty() }
                assertTrue { tagged.tags.isNotEmpty() }
            }
        }

        test("adding-an-existing-tag-replaces-that-tag") {
            val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for (replaced in taggedTerms.map { it.addTag(someKey, someValue2) }) {
                assertEquals(someValue2, replaced.getTag<TermTag<Int>>(someKey))
            }
        }

        test("setting-an-existing-tag-replaces-that-tag") {
            val taggedTerms = terms.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
            for (replaced in taggedTerms.map { it.setTag(someKey, someValue2) }) {
                assertEquals(someValue2, replaced.getTag<TermTag<Int>>(someKey))
            }
        }

        test("fresh-copy-preserves-tags") {
            alterationPreservesTags { freshCopy() }
        }

        test("fresh-copy-with-scope-preserves-tags") {
            alterationPreservesTags { freshCopy(Scope.Companion.of("Y")) }
        }

        test("apply-preserves-tags") {
            alterationPreservesTags { apply(Substitution.unifier(X, Integer.ONE)) }
        }

        test("get-preserves-tags") {
            alterationPreservesTags { apply(Substitution.unifier(X, Real.ONE)) }
        }
    })

private data class TermTag<T>(
    val value: T,
)
