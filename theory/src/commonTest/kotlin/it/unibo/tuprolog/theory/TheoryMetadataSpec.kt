package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags

class TheoryMetadataSpec :
    FunSpec({
        val theoryCases =
            listOf(
                TheoryKindCase("empty-indexed") { Theory.emptyIndexed(Unificator.default) },
                TheoryKindCase("indexed") { Theory.indexedOf(Unificator.default, it) },
                TheoryKindCase("mutable-empty-indexed") { MutableTheory.emptyIndexed(Unificator.default) },
                TheoryKindCase("mutable-indexed") { MutableTheory.indexedOf(Unificator.default, it) },
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
                .map { Rule.of(it.head.castToStruct().let { head -> head.setFunctor(head.functor + "'") }, it.body) }

        val presentClauses = TheoryUtils.wellFormedClauses

        fun accessingClausesFromATheoryPreservesClausesTags(
            property: String,
            access: Theory.(Clause) -> Sequence<Clause>,
        ) = theories(property) { case ->
            val untaggedTheory = case.theory()
            val taggedTheory = case.taggedTheory()
            taggedTheory shouldBe untaggedTheory
            taggedTheory.tags shouldNotBe untaggedTheory.tags
            untaggedTheory.tags.size shouldBe 0
            taggedTheory.tags.size shouldBe 1
            for (untaggedClause in untaggedTheory) {
                val untaggedClauses = untaggedTheory.clone().access(untaggedClause).toList()
                val taggedClauses = taggedTheory.clone().access(untaggedClause).toList()
                taggedClauses shouldBe untaggedClauses
                for ((untagged, tagged) in untaggedClauses.zip(taggedClauses)) {
                    tagged shouldBe untagged
                    tagged.tags shouldNotBe untagged.tags
                    untagged.tags.size shouldBe 0
                    tagged.tags.size shouldBe 2
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
            taggedTheory shouldBe untaggedTheory
            taggedTheory.tags shouldNotBe untaggedTheory.tags
            untaggedTheory.tags.size shouldBe 0
            taggedTheory.tags.size shouldBe 1
            for (clause in clausesSource) {
                val untaggedEdited = untaggedTheory.clone().edit(clause)
                val taggedEdited = taggedTheory.clone().edit(clause)
                taggedEdited shouldBe untaggedEdited
                taggedEdited.tags shouldNotBe untaggedEdited.tags
                untaggedEdited.tags.size shouldBe 0
                taggedEdited.tags.size shouldBe 1
                for (untagged in untaggedEdited) {
                    untagged.tags shouldBe emptyMap()
                }
                for (tagged in taggedEdited) {
                    if (clausesShouldBeUntaggedAfterEdit && tagged == clause) {
                        tagged.tags shouldBe clause.tags
                    } else {
                        tagged.tags.size shouldBe 2
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
            tagged shouldBe nonTagged
            tagged.hashCode() shouldBe nonTagged.hashCode()
            nonTagged.tags.isEmpty().shouldBeTrue()
            tagged.tags.isNotEmpty().shouldBeTrue()
        }

        theories("tags-stick-to-theories") { case ->
            val theory = case.theory()
            (theory.getTag<MetadataValue<Boolean?>>(someKey)).shouldBeNull()
            theory.containsTag(someKey).shouldBeFalse()
            val tagged = theory.setTag(someKey, someValue1)
            theory.containsTag(someKey).shouldBeFalse()
            (theory.getTag<MetadataValue<Boolean?>>(someKey)).shouldBeNull()
            tagged.containsTag(someKey).shouldBeTrue()
            (tagged.getTag<MetadataValue<Boolean?>>(someKey)) shouldBe someValue1
            theory.tags shouldBe emptyMap()
            tagged.tags shouldBe mapOf(someKey to someValue1)
        }

        theories("cloning-theories-preserves-tags") { case ->
            val taggedTheory = case.taggedTheory()
            val clone = taggedTheory.clone()
            clone shouldBe taggedTheory
            clone.tags shouldBe taggedTheory.tags
            for ((clause, clonedClause) in taggedTheory.zip(clone)) {
                clonedClause shouldBe clause
                clonedClause.tags shouldBe clause.tags
            }
        }

        theories("tags-can-be-replaced") { case ->
            val theory = case.theory().setTag(someKey, someValue1)
            val tags = mapOf<String, Any>(someKey to someValue2, someOtherKey to someOtherValue)
            val replaced = theory.replaceTags(tags)
            theory.tags shouldBe mapOf(someKey to someValue1)
            replaced.tags shouldBe tags
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
            cleared shouldBe tagged
            cleared.hashCode() shouldBe tagged.hashCode()
            cleared.tags.isEmpty().shouldBeTrue()
            tagged.tags.isNotEmpty().shouldBeTrue()
        }

        theories("adding-an-existing-tag-replaces-that-tag") { case ->
            val replaced =
                case
                    .theory()
                    .setTags(someKey to someValue1, someOtherKey to someOtherValue)
                    .addTag(someKey, someValue2)
            (replaced.getTag<MetadataValue<Boolean?>>(someKey)) shouldBe someValue2
        }

        theories("setting-an-existing-tag-replaces-that-tag") { case ->
            val replaced =
                case
                    .theory()
                    .setTags(someKey to someValue1, someOtherKey to someOtherValue)
                    .setTag(someKey, someValue2)
            (replaced.getTag<MetadataValue<Boolean?>>(someKey)) shouldBe someValue2
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
