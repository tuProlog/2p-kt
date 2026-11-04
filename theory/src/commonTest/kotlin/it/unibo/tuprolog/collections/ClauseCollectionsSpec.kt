package it.unibo.tuprolog.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.setTags

/*
 * The clause collections (#1019): ordered ones keep Prolog order and behave like lists, unordered ones compare as
 * multisets, sets and maps identify clauses up to variable renaming, every lookup goes by unification, and the stored
 * instances (with their tags) are the ones returned.
 */

private fun p(vararg args: Any): Clause =
    Fact.of(
        Struct.of(
            "p",
            args.map {
                when (it) {
                    is Int -> Integer.of(it)
                    is Double -> Real.of(it)
                    is String -> if (it[0].isUpperCase()) Var.of(it) else Atom.of(it)
                    else -> it as it.unibo.tuprolog.core.Term
                }
            },
        ),
    )

private val anyP: Clause = Fact.of(Struct.of("p", Var.anonymous()))

/** Sizes below and above the indexing threshold. */
private class SizeCase(
    val id: String,
    val size: Int,
)

private val sizes = listOf(SizeCase("scanned", 3), SizeCase("indexed", 20))

private fun clauses(size: Int): List<Clause> = List(size) { p(it) }

private class DequeCase(
    val id: String,
    val build: (List<Clause>) -> MutableClauseDeque,
)

private val mutableDeques =
    listOf(
        DequeCase("mutable-queue") { MutableClauseQueue.of(Unificator.default, it) as MutableClauseDeque },
        DequeCase("mutable-deque") { MutableClauseDeque.of(Unificator.default, it) },
        DequeCase("mutable-list") { MutableClauseList.of(Unificator.default, it) },
        DequeCase("mutable-theory") { MutableTheory.of(Unificator.default, it) },
    )

private class OrderedCase(
    val id: String,
    val build: (List<Clause>) -> ClauseQueue,
)

private val immutableOrdered =
    listOf(
        OrderedCase("queue") { ClauseQueue.of(Unificator.default, it) },
        OrderedCase("deque") { ClauseDeque.of(Unificator.default, it) },
        OrderedCase("list") { ClauseList.of(Unificator.default, it) },
        OrderedCase("theory") { Theory.of(Unificator.default, it) },
    )

class ClauseCollectionsSpec :
    FunSpec({
        for (size in sizes) {
            withTests(nameFn = { "ordered/${it.id}/lookups/${size.id}" }, immutableOrdered) { case ->
                val all = clauses(size.size) + p("X")
                val collection = case.build(all)
                collection.toList() shouldBe all
                collection.size shouldBe all.size.toLong()
                collection[p(1)].toList() shouldBe listOf(p(1), all.last())
                collection.firstMatching(p(2)) shouldBe p(2)
                collection.countMatching(anyP) shouldBe all.size.toLong()
                (p(1) in collection) shouldBe true
                (p("zzz") in collection) shouldBe true // unifies with p(X)
                collection.containsVariant(p("Y")) shouldBe true
                collection.containsVariant(p("zzz")) shouldBe false
                collection.first() shouldBe all.first()
                collection.toMutable().toList() shouldBe all
            }

            withTests(nameFn = { "ordered/${it.id}/updates/${size.id}" }, mutableDeques) { case ->
                val oracle = clauses(size.size).toMutableList()
                val deque = case.build(oracle)
                deque.add(p("a")).let { oracle.add(p("a")) }
                deque.addFirst(p("b")).let { oracle.add(0, p("b")) }
                deque.toList() shouldBe oracle
                deque.removeFirst() shouldBe oracle.removeAt(0)
                deque.removeLast() shouldBe oracle.removeAt(oracle.lastIndex)
                deque.removeFirstMatching(p(1)) shouldBe p(1).also { oracle.remove(it) }
                deque.removeAllMatching(p("X")) shouldBe oracle.toList().also { oracle.clear() }
                deque.isEmpty shouldBe true
                deque.removeFirst().shouldBeNull()
                deque.addAll(clauses(size.size)) shouldBe true
                deque.lastOrNull() shouldBe p(size.size - 1)
                deque.clear()
                deque.size shouldBe 0L
            }
        }

        test("list/positional") {
            val list = MutableClauseList.of(Unificator.default, clauses(10))
            list[3] shouldBe p(3)
            list.add(3, p("a"))
            list[3] shouldBe p("a")
            list[4] shouldBe p(3)
            (list.set(0, p("b"))) shouldBe p(0)
            list.removeAt(1) shouldBe p(1)
            list.toList() shouldBe listOf(p("b"), p(2), p("a")) + List(7) { p(it + 3) }
            list.indexOfFirstMatching(p(5)) shouldBe 5
            list.indexOfFirstMatching(p("zzz")) shouldBe -1
            shouldThrow<IndexOutOfBoundsException> { list[100] }
            ClauseList.of(Unificator.default, clauses(5)).asList() shouldBe clauses(5)
        }

        test("ordered/equality") {
            ClauseList.of(Unificator.default, clauses(3)) shouldBe MutableClauseQueue.of(Unificator.default, clauses(3))
            ClauseList.of(Unificator.default, clauses(3)) shouldNotBe
                ClauseList.of(Unificator.default, clauses(3).reversed())
            ClauseQueue.of(Unificator.default, clauses(3)).hashCode() shouldBe
                MutableClauseDeque.of(Unificator.default, clauses(3)).hashCode()
        }

        test("multiset/equality-ignores-order") {
            val clauses = clauses(5) + p(1)
            ClauseMultiSet.of(Unificator.default, clauses) shouldBe
                MutableClauseMultiSet.of(Unificator.default, clauses.reversed())
            ClauseMultiSet.of(Unificator.default, clauses) shouldNotBe ClauseMultiSet.of(Unificator.default, clauses(5))
            ClauseMultiSet.of(Unificator.default, clauses).countVariants(p(1)) shouldBe 2L
        }

        test("set/variants-are-not-added") {
            val set =
                MutableClauseSet.of(
                    Unificator.default,
                    p("X", "Y"),
                    p("A", "B"),
                    Var.of("A").let { p(it, it) },
                    p(1, 2),
                )
            set.size shouldBe 3L
            set.add(p("C", "D")) shouldBe false
            set.add(p("C", 2)) shouldBe true
            ClauseSet.of(Unificator.default, p("X"), p("Y"), p(1)).size shouldBe 2L
            set.toImmutable().toMutable().size shouldBe 4L
        }

        test("set/naive-unificator") {
            val set = MutableClauseSet.of(Unificator.naive(), clauses(20))
            set.add(p(1.0)) shouldBe true // not a variant of p(1), though they unify
            set[p(1)].toList() shouldBe listOf(p(1), p(1.0))
        }

        test("map/variant-keys-and-unification-lookups") {
            val map = MutableClauseMap.of(Unificator.default, p("X", 1) to "a", p(2, "Y") to "b", p(3, 3) to "c")
            map[p("Z", 1)] shouldBe "a"
            map[p(1, 1)].shouldBeNull()
            map.put(p("W", 1), "d") shouldBe "a"
            map.size shouldBe 3
            map.entriesMatching(p(2, 1)).map { it.second }.toList() shouldBe listOf("d", "b")
            map.remove(p(2, "K")) shouldBe "b"
            map.containsKey(p(2, "Y")) shouldBe false
            map.removeAllMatching(p(3, 3)).map { it.second } shouldBe listOf("c")
            map.asMap().values.toList() shouldBe listOf("d")
            map.keys.size shouldBe 1L
            ClauseMap.of(Unificator.default, p(1) to 1) shouldBe MutableClauseMap.of(Unificator.default, p(1) to 1)
            map.clear()
            map.isEmpty shouldBe true
        }

        test("tags-are-preserved") {
            val tagged = p("a").setTags(mapOf("k" to "v"))
            val collections =
                listOf(
                    ClauseList.of(Unificator.default, tagged),
                    MutableClauseList.of(Unificator.default, tagged),
                    ClauseSet.of(Unificator.default, tagged),
                    MutableClauseMultiSet.of(Unificator.default, tagged),
                    ClauseMap.of(Unificator.default, tagged to 1).keys,
                )
            for (collection in collections) {
                collection.single() shouldBeSameInstanceAs tagged
                collection[p("a")].single() shouldBeSameInstanceAs tagged
            }
        }

        test("immutable-conversion-is-identity") {
            val list = ClauseList.of(Unificator.default, clauses(3))
            list.toImmutable() shouldBeSameInstanceAs list
            val theory = MutableTheory.of(Unificator.default, clauses(3))
            theory.toMutable() shouldNotBe null
            (theory.toMutable() === theory) shouldBe false
        }
    })
