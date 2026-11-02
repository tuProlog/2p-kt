package it.unibo.tuprolog.utils

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

@OptIn(ExperimentalKotest::class)
private val config = PropTestConfig(seed = 1017L, iterations = 200)

private val lists = Arb.list(Arb.list(Arb.int(-20..20), 0..6), 0..5)

/** One way of calling `merge`/`mergeSequences`: they must all agree. */
private class MergeCase(
    val id: String,
    val merge: (List<List<Int>>, Comparator<Int>) -> Sequence<Int>,
)

private val mergeCases =
    listOf(
        MergeCase("merge/comparator-iterable") { ls, c -> merge(c, ls) },
        MergeCase("merge/iterable-lambda") { ls, c -> merge(ls) { a: Int, b: Int -> c.compare(a, b) } },
        MergeCase("merge/vararg-lambda") { ls, c -> merge(*ls.toTypedArray()) { a: Int, b: Int -> c.compare(a, b) } },
        MergeCase("merge/comparator-vararg") { ls, c -> merge(c, *ls.toTypedArray()) },
        MergeCase("merge/sequence-lambda") { ls, c -> merge(ls.asSequence()) { a: Int, b: Int -> c.compare(a, b) } },
        MergeCase("merge/comparator-sequence") { ls, c -> merge(c, ls.asSequence()) },
        MergeCase("merge-sequences/iterable-lambda") { ls, c ->
            mergeSequences(ls.map { it.asSequence() }) { a: Int, b: Int -> c.compare(a, b) }
        },
        MergeCase("merge-sequences/comparator-iterable") { ls, c -> mergeSequences(c, ls.map { it.asSequence() }) },
        MergeCase("merge-sequences/sequence-lambda") { ls, c ->
            mergeSequences(ls.asSequence().map { it.asSequence() }) { a: Int, b: Int -> c.compare(a, b) }
        },
        MergeCase("merge-sequences/comparator-sequence") { ls, c ->
            mergeSequences(c, ls.asSequence().map { it.asSequence() })
        },
        MergeCase("merge-sequences/vararg-lambda") { ls, c ->
            mergeSequences(*ls.map { it.asSequence() }.toTypedArray()) { a: Int, b: Int -> c.compare(a, b) }
        },
        MergeCase("merge-sequences/comparator-vararg") { ls, c ->
            mergeSequences(c, *ls.map { it.asSequence() }.toTypedArray())
        },
    )

/** One way of calling `interleave`/`interleaveSequences`: they must all agree. */
private class InterleaveCase(
    val id: String,
    val interleave: (List<List<Int>>) -> Sequence<Int>,
)

private val interleaveCases =
    listOf(
        InterleaveCase("interleave/iterable") { interleave(it) },
        InterleaveCase("interleave/vararg") { interleave(*it.toTypedArray()) },
        InterleaveCase("interleave/sequence") { interleave(it.asSequence()) },
        InterleaveCase(
            "interleave-sequences/vararg",
        ) { ls -> interleaveSequences(*ls.map { it.asSequence() }.toTypedArray()) },
        InterleaveCase(
            "interleave-sequences/sequence",
        ) { ls -> interleaveSequences(ls.asSequence().map { it.asSequence() }) },
        InterleaveCase("interleave-sequences/iterable") { ls -> interleaveSequences(ls.map { it.asSequence() }) },
    )

/** Round-robin by position: every first element, then every second one, and so on. */
private fun roundRobin(lists: List<List<Int>>): List<Int> =
    (0 until (lists.maxOfOrNull { it.size } ?: 0)).flatMap { i -> lists.mapNotNull { it.getOrNull(i) } }

private fun factorial(n: Int): Int = (1..n).fold(1) { acc, i -> acc * i }

class IterUtilsSpec :
    FunSpec({
        withTests(nameFn = { "${it.id}/yields-the-sorted-union-of-sorted-inputs" }, mergeCases) { case ->
            for (comparator in listOf(naturalOrder<Int>(), reverseOrder())) {
                checkAll(config, lists) { inputs ->
                    val sorted = inputs.map { it.sortedWith(comparator) }
                    case.merge(sorted, comparator).toList() shouldBe sorted.flatten().sortedWith(comparator)
                }
            }
        }

        withTests(nameFn = { "${it.id}/is-round-robin-by-position" }, interleaveCases) { case ->
            checkAll(config, lists) { inputs -> case.interleave(inputs).toList() shouldBe roundRobin(inputs) }
        }

        test("interleave/example") {
            interleave(listOf(1, 2, 3), listOf(10, 20), listOf(100)).toList() shouldBe listOf(1, 10, 100, 2, 20, 3)
        }

        test("permutations/are-all-distinct-rearrangements") {
            checkAll(config, Arb.int(0..5)) { n ->
                val items = (1..n).toList()
                val permutations = items.permutations().toList()
                permutations.size shouldBe maxOf(1, factorial(n))
                permutations.toSet().size shouldBe permutations.size
                permutations.forEach { it.sorted() shouldBe items }
            }
        }

        test("permutations/overloads-agree") {
            val expected = listOf(1, 2, 3).permutations().toList()
            permutations(1, 2, 3).toList() shouldBe expected
            setOf(1, 2, 3).permutations().toList() shouldBe expected
            sequenceOf(1, 2, 3).permutations().toList() shouldBe expected
        }

        test("subsequences/of-empty-is-the-empty-prefix") {
            (emptyList<Int>().subsequences().map { it.toList() }.toList()) shouldBe listOf(emptyList())
        }

        test("subsequences/are-the-growing-prefixes") {
            (subsequences(1, 2, 3).map { it.toList() }.toList()) shouldBe
                listOf(listOf(1), listOf(1, 2), listOf(1, 2, 3))
        }

        test("drop-last/drops-only-the-last-element") {
            checkAll(config, Arb.list(Arb.int(), 0..6)) { items ->
                items.asSequence().dropLast().toList() shouldBe items.dropLast(1)
            }
        }

        test("product/squared-pairs-every-element-with-every-element") {
            sequenceOf(1, 2).squared().toList() shouldBe listOf(1 to 1, 1 to 2, 2 to 1, 2 to 2)
            (sequenceOf(1, 2).squared { a, b -> a + b }.toList()) shouldBe listOf(2, 3, 3, 4)
        }

        test("long-indexed/pairs-elements-with-positions") {
            (sequenceOf("a", "b").longIndexed().map { it.index to it.value }.toList()) shouldBe
                listOf(0L to "a", 1L to "b")
        }

        test("insert-at/inserts-before-the-index") {
            sequenceOf(1, 2).insertAt(1, 9, 8).toList() shouldBe listOf(1, 9, 8, 2)
        }

        test("insert-at/past-the-end-appends") {
            sequenceOf(1, 2).insertAt(2, 9).toList() shouldBe listOf(1, 2, 9)
            sequenceOf(1, 2).insertAt(5, 9, 8).toList() shouldBe listOf(1, 2, 9, 8)
        }

        test("assert-items-are-not-null/passes-non-null-items") {
            (listOf<Int?>(1, 2).assertItemsAreNotNull()) shouldBe listOf(1, 2)
            (arrayOf<Int?>(1, 2).assertItemsAreNotNull().toList()) shouldBe listOf(1, 2)
            (sequenceOf<Int?>(1, 2).assertItemsAreNotNull().toList()) shouldBe listOf(1, 2)
            (listOf<Int?>(1, 2).asIterable().assertItemsAreNotNull().toList()) shouldBe listOf(1, 2)
        }

        test("assert-items-are-not-null/names-the-null-index") {
            val message = "Item at index 1 is null"
            (shouldThrow<IllegalArgumentException> { listOf(1, null).assertItemsAreNotNull() }.message) shouldBe message
            (shouldThrow<IllegalArgumentException> { arrayOf(1, null).assertItemsAreNotNull() }.message) shouldBe
                message
            (
                shouldThrow<IllegalArgumentException> {
                    sequenceOf(
                        1,
                        null,
                    ).assertItemsAreNotNull().toList()
                }.message
            ) shouldBe message
        }
    })
