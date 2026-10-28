package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TheoryMetadataSpec :
    FunSpec({
        val theoryCases =
            listOf(
                TheoryKindCase("empty-indexed") { Theory.emptyIndexed(Unificator.default) },
                TheoryKindCase("empty-listed") { Theory.emptyListed(Unificator.default) },
                TheoryKindCase("indexed") { Theory.indexedOf(Unificator.default, it) },
                TheoryKindCase("listed") { Theory.listedOf(Unificator.default, it) },
                TheoryKindCase("mutable-empty-indexed") { MutableTheory.emptyIndexed(Unificator.default) },
                TheoryKindCase("mutable-empty-listed") { MutableTheory.emptyListed(Unificator.default) },
                TheoryKindCase("mutable-indexed") { MutableTheory.indexedOf(Unificator.default, it) },
                TheoryKindCase("mutable-listed") { MutableTheory.listedOf(Unificator.default, it) },
            )

        val someKey = "some_key1"

        val someOtherKey = "some_key2"

        val yetAnotherKey = "another_key"

        val someValue1 = MetadataValue<Boolean?>(true)

        val someValue2 = MetadataValue<Boolean?>(false)

        val someOtherValue = MetadataValue<Boolean?>(null)

        fun TheoryKindCase.theory() = factory(TheoryUtils.wellFormedClauses)

        fun TheoryKindCase.taggedTheory() =
            factory(
                TheoryUtils.wellFormedClauses.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) },
            ).setTag(yetAnotherKey, someValue2)

        fun theories(
            property: String,
            test: (TheoryKindCase) -> Unit,
        ) = withTests(nameFn = { "$property/${it.id}" }, theoryCases) { test(it) }

        val missingClauses =
            TheoryUtils.wellFormedClauses
                .filterIsInstance<Rule>()
                .map { Rule.of(it.head.setFunctor(it.head.functor + "'"), it.body) }

        val presentClauses = TheoryUtils.wellFormedClauses

        fun accessingClausesFromATheoryPreservesClausesTags(
            property: String,
            access: Theory.(Clause) -> Sequence<Clause>,
        ) = theories(property) { case ->
            val untaggedTheory = case.theory()
            val taggedTheory = case.taggedTheory()
            assertEquals(untaggedTheory, taggedTheory)
            assertNotEquals(untaggedTheory.tags, taggedTheory.tags)
            assertEquals(0, untaggedTheory.tags.size)
            assertEquals(1, taggedTheory.tags.size)
            for (untaggedClause in untaggedTheory) {
                val untaggedClauses = untaggedTheory.clone().access(untaggedClause).toList()
                val taggedClauses = taggedTheory.clone().access(untaggedClause).toList()
                assertEquals(untaggedClauses, taggedClauses)
                for ((untagged, tagged) in untaggedClauses.zip(taggedClauses)) {
                    assertEquals(untagged, tagged)
                    assertNotEquals(untagged.tags, tagged.tags)
                    assertEquals(0, untagged.tags.size)
                    assertEquals(2, tagged.tags.size)
                }
            }
        }

        fun editingATheoryPreservesClausesTags(
            property: String,
            clausesSource: Iterable<Clause>,
            clausesShouldBeUntaggedAfterEdit: Boolean = true,
            edit: Theory.(Clause) -> Theory,
        ) = theories(property) { case ->
            val untaggedTheory = case.theory()
            val taggedTheory = case.taggedTheory()
            assertEquals(untaggedTheory, taggedTheory)
            assertNotEquals(untaggedTheory.tags, taggedTheory.tags)
            assertEquals(0, untaggedTheory.tags.size)
            assertEquals(1, taggedTheory.tags.size)
            for (clause in clausesSource) {
                val untaggedEdited = untaggedTheory.clone().edit(clause)
                val taggedEdited = taggedTheory.clone().edit(clause)
                assertEquals(untaggedEdited, taggedEdited)
                assertNotEquals(untaggedEdited.tags, taggedEdited.tags)
                assertEquals(0, untaggedEdited.tags.size)
                assertEquals(1, taggedEdited.tags.size)
                for (untagged in untaggedEdited) {
                    assertEquals(emptyMap(), untagged.tags)
                }
                for (tagged in taggedEdited) {
                    if (clausesShouldBeUntaggedAfterEdit && tagged == clause) {
                        assertEquals(clause.tags, tagged.tags)
                    } else {
                        assertEquals(2, tagged.tags.size)
                    }
                }
            }
        }

        fun taggingPreservesEquality(
            property: String,
            tag: Theory.() -> Theory,
        ) = theories(property) { case ->
            val nonTagged = case.theory()
            val tagged = case.theory().tag()
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }

        theories("tags-stick-to-theories") { case ->
            val theory = case.theory()
            assertNull(theory.getTag<MetadataValue<Boolean?>>(someKey))
            assertFalse { theory.containsTag(someKey) }
            val tagged = theory.setTag(someKey, someValue1)
            assertFalse { theory.containsTag(someKey) }
            assertNull(theory.getTag<MetadataValue<Boolean?>>(someKey))
            assertTrue { tagged.containsTag(someKey) }
            assertEquals(someValue1, tagged.getTag<MetadataValue<Boolean?>>(someKey))
            assertEquals(emptyMap(), theory.tags)
            assertEquals(mapOf(someKey to someValue1), tagged.tags)
        }

        theories("cloning-theories-preserves-tags") { case ->
            val taggedTheory = case.taggedTheory()
            val clone = taggedTheory.clone()
            assertEquals(taggedTheory, clone)
            assertEquals(taggedTheory.tags, clone.tags)
            for ((clause, clonedClause) in taggedTheory.zip(clone)) {
                assertEquals(clause, clonedClause)
                assertEquals(clause.tags, clonedClause.tags)
            }
        }

        theories("tags-can-be-replaced") { case ->
            val theory = case.theory().setTag(someKey, someValue1)
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = theory.replaceTags(tags)
            assertEquals(mapOf(someKey to someValue1), theory.tags)
            assertEquals(tags, replaced.tags)
        }

        taggingPreservesEquality("adding-tags-preserves-equality") { addTag(someKey, someValue1) }

        taggingPreservesEquality("summing-tags-preserves-equality") { this + (someKey to someValue1) }

        taggingPreservesEquality("setting-tags-preserves-equality") { setTag(someKey, someValue1) }

        taggingPreservesEquality("setting-multiple-tags-preserves-equality") {
            setTags(someKey to someValue1, someOtherKey to someOtherValue)
        }

        theories("clearing-tags-preserves-equality") { case ->
            val tagged = case.theory().setTags(someKey to someValue1, someOtherKey to someOtherValue)
            val cleared = tagged.clearTags()
            assertEquals(tagged, cleared)
            assertEquals(tagged.hashCode(), cleared.hashCode())
            assertTrue { cleared.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }

        theories("adding-an-existing-tag-replaces-that-tag") { case ->
            val replaced =
                case
                    .theory()
                    .setTags(someKey to someValue1, someOtherKey to someOtherValue)
                    .addTag(someKey, someValue2)
            assertEquals(someValue2, replaced.getTag<MetadataValue<Boolean?>>(someKey))
        }

        theories("setting-an-existing-tag-replaces-that-tag") { case ->
            val replaced =
                case
                    .theory()
                    .setTags(someKey to someValue1, someOtherKey to someOtherValue)
                    .setTag(someKey, someValue2)
            assertEquals(someValue2, replaced.getTag<MetadataValue<Boolean?>>(someKey))
        }

        accessingClausesFromATheoryPreservesClausesTags("reading-clauses-from-a-theory-preserves-clauses-tags") {
            get(it)
        }

        accessingClausesFromATheoryPreservesClausesTags("retracting-one-clause-from-a-theory-preserves-clauses-tags") {
            retract(it).theory.asSequence()
        }

        accessingClausesFromATheoryPreservesClausesTags("retracting-all-clause-from-a-theory-preserves-clauses-tags") {
            retractAll(it).theory.asSequence()
        }

        editingATheoryPreservesClausesTags(
            "adding-one-clause-at-the-end-of-a-theory-preserves-clauses-and-theory-tags",
            missingClauses,
        ) { assertZ(it) }

        editingATheoryPreservesClausesTags(
            "adding-several-clauses-at-the-end-of-a-theory-preserves-clauses-and-theory-tags",
            missingClauses,
        ) { assertZ(listOf(it, it)) }

        editingATheoryPreservesClausesTags(
            "adding-one-clause-at-the-beginning-of-a-theory-preserves-clauses-and-theory-tags",
            missingClauses,
        ) { assertA(it) }

        editingATheoryPreservesClausesTags(
            "adding-several-clauses-at-the-beginning-of-a-theory-preserves-clauses-and-theory-tags",
            missingClauses,
        ) { assertA(listOf(it, it)) }

        editingATheoryPreservesClausesTags(
            "retracting-one-clause-from-a-theory-preserves-clauses-and-theory-tags",
            presentClauses,
            false,
        ) { retract(it).theory }

        editingATheoryPreservesClausesTags(
            "retracting-all-clauses-from-a-theory-preserves-clauses-and-theory-tags",
            presentClauses,
            false,
        ) { retractAll(it).theory }
    })

private class TheoryKindCase(
    val id: String,
    val factory: (Iterable<Clause>) -> Theory,
)

private data class MetadataValue<T>(
    val value: T,
)
