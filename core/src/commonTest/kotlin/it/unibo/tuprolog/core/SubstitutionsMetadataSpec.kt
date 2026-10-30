@file:Suppress("PrivatePropertyName", "ktlint:standard:property-naming")

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

private class TaggableSubstitutionCase(
    val id: String,
    val substitution: Substitution,
)

class SubstitutionsMetadataSpec :
    FunSpec({
        val X = Var.of("X")

        val Y = Var.of("Y")

        val Z = Var.of("Z")

        val assignedTerms: kotlin.collections.List<Pair<String, () -> Term>> =
            listOf(
                "variable" to { X },
                "anonymous-variable" to { Var.anonymous() },
                "atom" to { Atom.of("atom") },
                "integer" to { Integer.ONE },
                "real" to { Real.ONE_TENTH },
                "empty-list" to { EmptyList() },
                "empty-block" to { EmptyBlock() },
                "cons" to { Cons.of(Real.ONE_HALF, X) },
                "struct" to { Struct.of("g", X) },
                "list" to { List.of(X) },
                "block" to { Block.of(X) },
                "tuple" to { Tuple.of(X, X) },
                "fact-of-struct" to { Fact.of(Struct.of("f", X)) },
                "fact-of-list" to { Fact.of(List.of(X)) },
                "fact-of-block" to { Fact.of(Block.of(X)) },
                "fact-of-tuple" to { Fact.of(Tuple.of(X, X)) },
                "directive" to { Directive.of(Struct.of("h", X), Tuple.of(X, X)) },
                "rule" to { Rule.of(Struct.of("h", X), Tuple.of(X, X)) },
            )

        val substitutionCases =
            listOf(
                TaggableSubstitutionCase("failed", Substitution.failed()),
                TaggableSubstitutionCase("empty", Substitution.empty()),
            ) +
                assignedTerms.map { (id, term) ->
                    TaggableSubstitutionCase("unifier-to-$id", Substitution.unifier(Y, term()))
                } +
                assignedTerms.map { (id, term) -> TaggableSubstitutionCase("of-to-$id", Substitution.of(Z, term())) }

        val someKey = "some_key1"

        val someOtherKey = "some_key2"

        val yetAnotherKey = "some_key3"

        val someValue1 = SubstitutionTag(0L)

        val someValue2 = SubstitutionTag(1L)

        val someOtherValue = SubstitutionTag(2L)

        fun substitutions(
            property: String,
            test: (Substitution) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, substitutionCases) { test(it.substitution) }

        fun alteringPreservesTags(
            property: String,
            alter: Substitution.() -> Substitution,
        ) = substitutions(property) {
            val tags = mapOf(someKey to someValue1, someOtherKey to someValue2)
            val substitution = it.setTags(tags)
            assertEquals(tags, substitution.tags)
            assertEquals(substitution.tags, substitution.alter().tags)
        }

        fun summingMergesTags(
            property: String,
            other: Substitution,
            sum: Substitution.(Substitution) -> Substitution,
        ) = substitutions(property) {
            val tags = mapOf(someKey to someValue1, someOtherKey to someValue2)
            val substitution = it.setTags(tags)
            assertEquals(tags, substitution.tags)
            assertEquals(tags + other.tags, substitution.sum(other).tags)
        }

        substitutions("tags-stick-to-substitutions") { substitution ->
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

        substitutions("tags-can-be-replaced") {
            val substitution = it.setTag(someKey, someValue1)
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = substitution.replaceTags(tags)
            assertEquals(mapOf(someKey to someValue1), substitution.tags)
            assertEquals(tags, replaced.tags)
        }

        fun taggingPreservesEquality(
            property: String,
            tag: Substitution.() -> Substitution,
        ) = substitutions(property) { nonTagged ->
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

        substitutions("clearing-tags-preserves-equality") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val cleared = tagged.clearTags()
            assertEquals(tagged, cleared)
            assertEquals(tagged.hashCode(), cleared.hashCode())
            assertTrue { cleared.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }

        substitutions("adding-an-existing-tag-replaces-that-tag") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val replaced = tagged.addTag(someKey, someValue2)
            assertEquals(someValue2, replaced.getTag<SubstitutionTag<Long>>(someKey))
        }

        substitutions("setting-an-existing-tag-replaces-that-tag") {
            val tagged = it.setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val replaced = tagged.setTag(someKey, someValue2)
            assertEquals(someValue2, replaced.getTag<SubstitutionTag<Long>>(someKey))
        }

        alteringPreservesTags("minus-iterable-preserves-tags") { this - listOf(X, Y, Z) }

        alteringPreservesTags("minus-preserves-tags") { this - X - Y - Z }

        alteringPreservesTags("filter-with-function-preserves-tags") { filter { (k, _) -> k in setOf(X, Y, Z) } }

        alteringPreservesTags("filter-preserves-tags") { filter(setOf(X, Y, Z)) }

        summingMergesTags("plus-preserves-tags", Substitution.empty().setTag(yetAnotherKey, someOtherValue)) {
            plus(it)
        }

        summingMergesTags(
            "plus-with-function-preserves-tags",
            Substitution.of(X to EmptyBlock()).setTag(yetAnotherKey, someOtherValue),
        ) {
            plus(it) { x, y -> x + y }
        }
    })

private data class SubstitutionTag<T>(
    val value: T,
)
