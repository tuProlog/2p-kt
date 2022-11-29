package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestTheoryMetadata {

    private val theories get() = listOf<Theory>(
        Theory.emptyIndexed(Unificator.default),
        Theory.emptyListed(Unificator.default),
        Theory.indexedOf(Unificator.default, TheoryUtils.wellFormedClauses),
        Theory.listedOf(Unificator.default, TheoryUtils.wellFormedClauses),
        MutableTheory.emptyIndexed(Unificator.default),
        MutableTheory.emptyListed(Unificator.default),
        MutableTheory.indexedOf(Unificator.default, TheoryUtils.wellFormedClauses),
        MutableTheory.listedOf(Unificator.default, TheoryUtils.wellFormedClauses),
    )

    private data class Metadata<T>(val value: T)

    private val someKey = "some_key1"
    private val someOtherKey = "some_key2"
    private val yetAnotherKey = "another_key"
    private val someValue1 = Metadata<Boolean?>(true)
    private val someValue2 = Metadata<Boolean?>(false)
    private val someOtherValue = Metadata<Boolean?>(null)

    private val taggedTheories get() = TheoryUtils.wellFormedClauses
        .map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        .let {
            listOf(
                Theory.emptyIndexed(Unificator.default).setTag(yetAnotherKey, someValue2),
                Theory.emptyListed(Unificator.default).setTag(yetAnotherKey, someValue2),
                Theory.indexedOf(Unificator.default, it).setTag(yetAnotherKey, someValue2),
                Theory.listedOf(Unificator.default, it).setTag(yetAnotherKey, someValue2),
                MutableTheory.emptyIndexed(Unificator.default).setTag(yetAnotherKey, someValue2),
                MutableTheory.emptyListed(Unificator.default).setTag(yetAnotherKey, someValue2),
                MutableTheory.indexedOf(Unificator.default, it).setTag(yetAnotherKey, someValue2),
                MutableTheory.listedOf(Unificator.default, it).setTag(yetAnotherKey, someValue2)
            )
        }

    private val missingClauses = TheoryUtils.wellFormedClauses
        .filterIsInstance<Rule>()
        .map { Rule.of(it.head.setFunctor(it.head.functor + "'"), it.body) }

    private val presentClauses = TheoryUtils.wellFormedClauses

    @Test
    fun tagsStickToTheories() {
        for (theory in theories) {
            assertNull(theory.getTag<Metadata<Boolean?>>(someKey))
            assertFalse { theory.containsTag(someKey) }
            val tagged = theory.setTag(someKey, someValue1)
            assertFalse { theory.containsTag(someKey) }
            assertNull(theory.getTag<Metadata<Boolean?>>(someKey))
            assertTrue { tagged.containsTag(someKey) }
            assertEquals(someValue1, tagged.getTag<Metadata<Boolean?>>(someKey))
            assertEquals(emptyMap(), theory.tags)
            assertEquals(mapOf(someKey to someValue1), tagged.tags)
        }
    }

    @Test
    fun cloningTheoriesPreservesTags() {
        for (taggedTheory in taggedTheories) {
            val clone = taggedTheory.clone()
            assertEquals(taggedTheory, clone)
            assertEquals(taggedTheory.tags, clone.tags)
            for ((clause, clonedClause) in taggedTheory.zip(clone)) {
                assertEquals(clause, clonedClause)
                assertEquals(clause.tags, clonedClause.tags)
            }
        }
    }

    @Test
    fun tagsCanBeReplaced() {
        for (theory in theories.map { it.setTag(someKey, someValue1) }) {
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = theory.replaceTags(tags)
            assertEquals(mapOf(someKey to someValue1), theory.tags)
            assertEquals(tags, replaced.tags)
        }
    }

    @Test
    fun addingTagsPreservesEquality() {
        for ((nonTagged, tagged) in theories.zip(theories.map { it.addTag(someKey, someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun summingTagsPreservesEquality() {
        for ((nonTagged, tagged) in theories.zip(theories.map { it + (someKey to someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun settingTagsPreservesEquality() {
        for ((nonTagged, tagged) in theories.zip(theories.map { it.setTag(someKey, someValue1) })) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun settingMultipleTagsPreservesEquality() {
        val taggedTheory = theories.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for ((nonTagged, tagged) in theories.zip(taggedTheory)) {
            assertEquals(nonTagged, tagged)
            assertEquals(nonTagged.hashCode(), tagged.hashCode())
            assertTrue { nonTagged.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun clearingTagsPreservesEquality() {
        val taggedTheory = theories.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for ((tagged, cleared) in taggedTheory.zip(taggedTheory.map { it.clearTags() })) {
            assertEquals(tagged, cleared)
            assertEquals(tagged.hashCode(), cleared.hashCode())
            assertTrue { cleared.tags.isEmpty() }
            assertTrue { tagged.tags.isNotEmpty() }
        }
    }

    @Test
    fun addingAnExistingTagReplacesThatTag() {
        val taggedTheory = theories.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedTheory.map { it.addTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Boolean?>>(someKey))
        }
    }

    @Test
    fun settingAnExistingTagReplacesThatTag() {
        val taggedTheory = theories.map { it.setTags(someKey to someValue1, someOtherKey to someOtherValue) }
        for (replaced in taggedTheory.map { it.setTag(someKey, someValue2) }) {
            assertEquals(someValue2, replaced.getTag<Metadata<Boolean?>>(someKey))
        }
    }

    private fun accessingClausesFromATheoryPreservesClausesTags(access: Theory.(Clause) -> Sequence<Clause>) {
        for ((untaggedTheory, taggedTheory) in theories.zip(taggedTheories)) {
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
    }

    private fun editingATheoryPreservesClausesTags(
        clausesSource: Iterable<Clause>,
        clausesShouldBeUntaggedAfterEdit: Boolean = true,
        edit: Theory.(Clause) -> Theory
    ) {
        for ((untaggedTheory, taggedTheory) in theories.zip(taggedTheories)) {
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
    }

    @Test
    fun readingClausesFromATheoryPreservesClausesTags() {
        accessingClausesFromATheoryPreservesClausesTags { get(it) }
    }

    @Test
    fun retractingOneClauseFromATheoryPreservesClausesTags() {
        accessingClausesFromATheoryPreservesClausesTags { retract(it).theory.asSequence() }
    }

    @Test
    fun retractingAllClauseFromATheoryPreservesClausesTags() {
        accessingClausesFromATheoryPreservesClausesTags { retractAll(it).theory.asSequence() }
    }

    @Test
    fun addingOneClauseAtTheEndOfATheoryPreservesClausesAndTheoryTags() {
        editingATheoryPreservesClausesTags(missingClauses) { assertZ(it) }
    }

    @Test
    fun addingSeveralClausesAtTheEndOfATheoryPreservesClausesAndTheoryTags() {
        editingATheoryPreservesClausesTags(missingClauses) { assertZ(listOf(it, it)) }
    }

    @Test
    fun addingOneClauseAtTheBeginningOfATheoryPreservesClausesAndTheoryTags() {
        editingATheoryPreservesClausesTags(missingClauses) { assertA(it) }
    }

    @Test
    fun addingSeveralClausesAtTheBeginningOfATheoryPreservesClausesAndTheoryTags() {
        editingATheoryPreservesClausesTags(missingClauses) { assertA(listOf(it, it)) }
    }

    @Test
    fun retractingOneClauseFromATheoryPreservesClausesAndTheoryTags() {
        editingATheoryPreservesClausesTags(presentClauses, false) { retract(it).theory }
    }

    @Test
    fun retractingAllClausesFromATheoryPreservesClausesAndTheoryTags() {
        editingATheoryPreservesClausesTags(presentClauses, false) { retractAll(it).theory }
    }
}
