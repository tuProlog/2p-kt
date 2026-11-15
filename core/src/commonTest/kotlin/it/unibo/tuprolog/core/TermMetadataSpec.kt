package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags

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
                TaggableTermCase("directive", Goal.of(Struct.of("h", X), Tuple.of(X, X))),
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
        ) = terms(property) { term -> term.alter().tags shouldBe term.tags }

        terms("tags-stick-to-terms") { term ->
            (term.getTag<TermTag<Int>>(someKey)).shouldBeNull()
            term.containsTag(someKey).shouldBeFalse()
            val tagged = term.setTag(someKey, someValue1)
            term.containsTag(someKey).shouldBeFalse()
            (term.getTag<TermTag<Int>>(someKey)).shouldBeNull()
            tagged.containsTag(someKey).shouldBeTrue()
            (tagged.getTag<TermTag<Int>>(someKey)) shouldBe someValue1
            term.tags shouldBe emptyMap()
            tagged.tags shouldBe mapOf(someKey to someValue1)
        }

        terms("tags-can-be-replaced") {
            val term = it.setTag(someKey, someValue1)
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = term.replaceTags(tags)
            term.tags shouldBe mapOf(someKey to someValue1)
            replaced.tags shouldBe tags
        }

        fun taggingPreservesEquality(
            property: String,
            tag: Term.() -> Term,
        ) = terms(property) { nonTagged ->
            val tagged = nonTagged.tag()
            tagged shouldBe nonTagged
            tagged.hashCode() shouldBe nonTagged.hashCode()
            nonTagged.tags.isEmpty().shouldBeTrue()
            tagged.tags.isNotEmpty().shouldBeTrue()
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
            cleared shouldBe tagged
            cleared.hashCode() shouldBe tagged.hashCode()
            cleared.tags.isEmpty().shouldBeTrue()
            tagged.tags.isNotEmpty().shouldBeTrue()
        }

        terms("adding-an-existing-tag-replaces-that-tag") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val replaced = tagged.addTag(someKey, someValue2)
            (replaced.getTag<TermTag<Int>>(someKey)) shouldBe someValue2
        }

        terms("setting-an-existing-tag-replaces-that-tag") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val replaced = tagged.setTag(someKey, someValue2)
            (replaced.getTag<TermTag<Int>>(someKey)) shouldBe someValue2
        }

        alterationPreservesTags("fresh-copy-preserves-tags") { freshCopy() }

        alterationPreservesTags("fresh-copy-with-scope-preserves-tags") { freshCopy(Scope.Companion.of("Y")) }

        alterationPreservesTags("apply-preserves-tags") { apply(Substitution.unifier(X, Integer.ONE)) }

        alterationPreservesTags("get-preserves-tags") { apply(Substitution.unifier(X, Real.ONE)) }
    })

private data class TermTag<T>(
    val value: T,
)
