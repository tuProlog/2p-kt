package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.addTag
import it.unibo.tuprolog.utils.clearTags
import it.unibo.tuprolog.utils.plus
import it.unibo.tuprolog.utils.setTag
import it.unibo.tuprolog.utils.setTags

class SolutionMetadataSpec :
    FunSpec({
        withTests(nameFn = { "tags-stick-to-solutions/${it.id}" }, solutionCases) { case ->
            val solution = case.solution()
            (solution.getTag<Metadata<String>>(SOME_KEY)).shouldBeNull()
            solution.containsTag(SOME_KEY).shouldBeFalse()
            val tagged = solution.setTag(SOME_KEY, someValue1)
            solution.containsTag(SOME_KEY).shouldBeFalse()
            (solution.getTag<Metadata<String>>(SOME_KEY)).shouldBeNull()
            tagged.containsTag(SOME_KEY).shouldBeTrue()
            (tagged.getTag<Metadata<String>>(SOME_KEY)) shouldBe someValue1
            solution.tags shouldBe emptyMap()
            tagged.tags shouldBe mapOf(SOME_KEY to someValue1)
        }

        withTests(nameFn = { "solutions-preserve-query-tags/${it.id}" }, solutionCases) { case ->
            val solution = case.solution()
            solution.query.tags shouldBe mapOf(YET_ANOTHER_KEY to someValue2)
            if (solution is Solution.Yes) {
                solution.solvedQuery.tags shouldBe mapOf(YET_ANOTHER_KEY to someValue2)
            } else {
                solution.solvedQuery.shouldBeNull()
            }
        }

        withTests(nameFn = { "tags-can-be-replaced/${it.id}" }, solutionCases) { case ->
            val solution = case.solution().setTag(SOME_KEY, someValue1)
            val tags = mapOf<String, Any>(SOME_KEY to someValue2, SOME_OTHER_KEY to someOtherValue)
            val replaced = solution.replaceTags(tags)
            solution.tags shouldBe mapOf(SOME_KEY to someValue1)
            replaced.tags shouldBe tags
        }

        fun assertTaggingPreservesEquality(
            nonTagged: Solution,
            tagged: Solution,
        ) {
            tagged shouldBe nonTagged
            tagged.hashCode() shouldBe nonTagged.hashCode()
            nonTagged.tags.isEmpty().shouldBeTrue()
            tagged.tags.isNotEmpty().shouldBeTrue()
        }

        withTests(nameFn = { "adding-tags-preserves-equality/${it.id}" }, solutionCases) { case ->
            val solution = case.solution()
            assertTaggingPreservesEquality(solution, solution.addTag(SOME_KEY, someValue1))
        }

        withTests(nameFn = { "summing-tags-preserves-equality/${it.id}" }, solutionCases) { case ->
            val solution = case.solution()
            assertTaggingPreservesEquality(solution, solution + (SOME_KEY to someValue1))
        }

        withTests(nameFn = { "setting-tags-preserves-equality/${it.id}" }, solutionCases) { case ->
            val solution = case.solution()
            assertTaggingPreservesEquality(solution, solution.setTag(SOME_KEY, someValue1))
        }

        withTests(nameFn = { "setting-multiple-tags-preserves-equality/${it.id}" }, solutionCases) { case ->
            val solution = case.solution()
            assertTaggingPreservesEquality(
                solution,
                solution.setTags(SOME_KEY to someValue1, SOME_OTHER_KEY to someOtherValue),
            )
        }

        withTests(nameFn = { "clearing-tags-preserves-equality/${it.id}" }, solutionCases) { case ->
            val tagged = case.solution().setTags(SOME_KEY to someValue1, SOME_OTHER_KEY to someOtherValue)
            val cleared = tagged.clearTags()
            cleared shouldBe tagged
            cleared.hashCode() shouldBe tagged.hashCode()
            cleared.tags.isEmpty().shouldBeTrue()
            tagged.tags.isNotEmpty().shouldBeTrue()
        }

        withTests(nameFn = { "adding-an-existing-tag-replaces-that-tag/${it.id}" }, solutionCases) { case ->
            val tagged = case.solution().setTags(SOME_KEY to someValue1, SOME_OTHER_KEY to someOtherValue)
            (tagged.addTag(SOME_KEY, someValue2).getTag<Metadata<String>>(SOME_KEY)) shouldBe someValue2
        }

        withTests(nameFn = { "setting-an-existing-tag-replaces-that-tag/${it.id}" }, solutionCases) { case ->
            val tagged = case.solution().setTags(SOME_KEY to someValue1, SOME_OTHER_KEY to someOtherValue)
            (tagged.setTag(SOME_KEY, someValue2).getTag<Metadata<String>>(SOME_KEY)) shouldBe someValue2
        }
    })

private const val SOME_KEY = "some_key1"

private const val SOME_OTHER_KEY = "some_key2"

private const val YET_ANOTHER_KEY = "some_key3"

private val someValue1 = Metadata("1")

private val someValue2 = Metadata("2")

private val someOtherValue = Metadata("3")

private class QueryShape(
    val id: String,
    val query: (Var) -> Struct,
)

private class SolutionKind(
    val id: String,
    val solution: (Struct, Var) -> Solution,
)

private class SolutionCase(
    val id: String,
    val solution: () -> Solution,
)

private val queryShapes =
    listOf(
        QueryShape("q-of-var") { Struct.of("q", it) },
        QueryShape("q-of-anonymous-var") { Struct.of("q", Var.anonymous()) },
        QueryShape("atom") { Atom.of("atom") },
        QueryShape("q-of-integer") { Struct.of("q", Integer.ONE) },
        QueryShape("q-of-real") { Struct.of("q", Real.ONE_TENTH) },
        QueryShape("empty-list") { EmptyList() },
        QueryShape("empty-block") { EmptyBlock() },
        QueryShape("cons") { Cons.of(Real.ONE_HALF, it) },
        QueryShape("g-of-var") { Struct.of("g", it) },
        QueryShape("list") { List.of(it) },
        QueryShape("block") { Block.of(it) },
        QueryShape("tuple") { Tuple.of(it, it) },
    )

private val solutionKinds =
    listOf(
        SolutionKind("no") { query, _ -> Solution.no(query) },
        SolutionKind("halt") { query, _ -> Solution.halt(query, Dummy.RuntimeException) },
        SolutionKind("yes") { query, _ -> Solution.yes(query) },
        SolutionKind("yes-with-substitution") { query, x -> Solution.yes(query, Substitution.of(x, Integer.ONE)) },
    )

private val solutionCases =
    queryShapes.flatMap { shape ->
        solutionKinds.map { kind ->
            SolutionCase("${shape.id}/${kind.id}") {
                val x = Var.of("X")
                kind.solution(shape.query(x).setTag(YET_ANOTHER_KEY, someValue2), x)
            }
        }
    }

private data class Metadata<T>(
    val value: T,
)
