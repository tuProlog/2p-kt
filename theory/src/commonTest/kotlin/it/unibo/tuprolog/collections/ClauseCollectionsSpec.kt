package it.unibo.tuprolog.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.setTags

/*
 * The clause collections: ordered ones keep Prolog order and behave like lists, unordered ones compare as
 * multisets, sets and maps identify clauses up to variable renaming, every lookup goes by unification, and the stored
 * instances (with their tags) are the ones returned.
 */

// reals are passed as terms: on Kotlin/JS, `1.0 is Int` holds
private fun p(vararg args: Any): Clause =
    Fact.of(
        Struct.of(
            "p",
            args.map {
                when (it) {
                    is Int -> Integer.of(it)
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

/** One companion factory of a collection type: [build] gets the unificator and the clauses to pass. */
private class FactoryCase(
    val id: String,
    val mutable: Boolean,
    val ordered: Boolean,
    // whether the factory ignores the clauses (empty) or the unificator (default one)
    val empty: Boolean = false,
    val defaultUnificator: Boolean = false,
    val build: (Unificator, List<Clause>) -> ClauseCollection,
)

private fun factoryCases(
    id: String,
    mutable: Boolean,
    ordered: Boolean,
    empty: (Unificator) -> ClauseCollection,
    ofVararg: (Unificator, Array<Clause>) -> ClauseCollection,
    ofIterable: (Unificator, Iterable<Clause>) -> ClauseCollection,
    ofSequence: (Unificator, Sequence<Clause>) -> ClauseCollection,
) = listOf(
    FactoryCase("$id/empty", mutable, ordered, empty = true) { u, _ -> empty(u) },
    FactoryCase("$id/of-vararg", mutable, ordered) { u, cs -> ofVararg(u, cs.toTypedArray()) },
    FactoryCase("$id/of-iterable", mutable, ordered) { u, cs -> ofIterable(u, cs) },
    FactoryCase("$id/of-sequence", mutable, ordered) { u, cs -> ofSequence(u, cs.asSequence()) },
)

private fun scoped(clauses: List<Clause>): Array<Scope.() -> Clause> =
    clauses.map<Clause, Scope.() -> Clause> { clause -> { clause } }.toTypedArray()

private val factories: List<FactoryCase> =
    factoryCases(
        "queue",
        false,
        true,
        { ClauseQueue.empty(it) },
        { u, cs -> ClauseQueue.of(u, *cs) },
        { u, cs -> ClauseQueue.of(u, cs) },
        { u, cs -> ClauseQueue.of(u, cs) },
    ) +
        factoryCases(
            "deque",
            false,
            true,
            { ClauseDeque.empty(it) },
            { u, cs -> ClauseDeque.of(u, *cs) },
            { u, cs -> ClauseDeque.of(u, cs) },
            { u, cs -> ClauseDeque.of(u, cs) },
        ) +
        factoryCases(
            "list",
            false,
            true,
            { ClauseList.empty(it) },
            { u, cs -> ClauseList.of(u, *cs) },
            { u, cs -> ClauseList.of(u, cs) },
            { u, cs -> ClauseList.of(u, cs) },
        ) +
        factoryCases(
            "multiset",
            false,
            false,
            { ClauseMultiSet.empty(it) },
            { u, cs -> ClauseMultiSet.of(u, *cs) },
            { u, cs -> ClauseMultiSet.of(u, cs) },
            { u, cs -> ClauseMultiSet.of(u, cs) },
        ) +
        factoryCases(
            "set",
            false,
            false,
            { ClauseSet.empty(it) },
            { u, cs -> ClauseSet.of(u, *cs) },
            { u, cs -> ClauseSet.of(u, cs) },
            { u, cs -> ClauseSet.of(u, cs) },
        ) +
        factoryCases(
            "mutable-queue",
            true,
            true,
            { MutableClauseQueue.empty(it) },
            { u, cs -> MutableClauseQueue.of(u, *cs) },
            { u, cs -> MutableClauseQueue.of(u, cs) },
            { u, cs -> MutableClauseQueue.of(u, cs) },
        ) +
        factoryCases(
            "mutable-deque",
            true,
            true,
            { MutableClauseDeque.empty(it) },
            { u, cs -> MutableClauseDeque.of(u, *cs) },
            { u, cs -> MutableClauseDeque.of(u, cs) },
            { u, cs -> MutableClauseDeque.of(u, cs) },
        ) +
        factoryCases(
            "mutable-list",
            true,
            true,
            { MutableClauseList.empty(it) },
            { u, cs -> MutableClauseList.of(u, *cs) },
            { u, cs -> MutableClauseList.of(u, cs) },
            { u, cs -> MutableClauseList.of(u, cs) },
        ) +
        factoryCases(
            "mutable-multiset",
            true,
            false,
            { MutableClauseMultiSet.empty(it) },
            { u, cs -> MutableClauseMultiSet.of(u, *cs) },
            { u, cs -> MutableClauseMultiSet.of(u, cs) },
            { u, cs -> MutableClauseMultiSet.of(u, cs) },
        ) +
        factoryCases(
            "mutable-set",
            true,
            false,
            { MutableClauseSet.empty(it) },
            { u, cs -> MutableClauseSet.of(u, *cs) },
            { u, cs -> MutableClauseSet.of(u, cs) },
            { u, cs -> MutableClauseSet.of(u, cs) },
        ) +
        factoryCases(
            "theory",
            false,
            true,
            { Theory.empty(it) },
            { u, cs -> Theory.of(u, *cs) },
            { u, cs -> Theory.of(u, cs) },
            { u, cs -> Theory.of(u, cs) },
        ) +
        factoryCases(
            "theory/indexed",
            false,
            true,
            { Theory.emptyIndexed(it) },
            { u, cs -> Theory.indexedOf(u, *cs) },
            { u, cs -> Theory.indexedOf(u, cs) },
            { u, cs -> Theory.indexedOf(u, cs) },
        ) +
        factoryCases(
            "mutable-theory",
            true,
            true,
            { MutableTheory.empty(it) },
            { u, cs -> MutableTheory.of(u, *cs) },
            { u, cs -> MutableTheory.of(u, cs) },
            { u, cs -> MutableTheory.of(u, cs) },
        ) +
        factoryCases(
            "mutable-theory/indexed",
            true,
            true,
            { MutableTheory.emptyIndexed(it) },
            { u, cs -> MutableTheory.indexedOf(u, *cs) },
            { u, cs -> MutableTheory.indexedOf(u, cs) },
            { u, cs -> MutableTheory.indexedOf(u, cs) },
        ) +
        listOf(
            FactoryCase("theory/of-scopes", false, true) { u, cs -> Theory.of(u, *scoped(cs)) },
            FactoryCase("theory/indexed-of-scopes", false, true) { u, cs -> Theory.indexedOf(u, *scoped(cs)) },
            FactoryCase("mutable-theory/of-scopes", true, true) { u, cs -> MutableTheory.of(u, *scoped(cs)) },
            FactoryCase("mutable-theory/indexed-of-scopes", true, true) { u, cs ->
                MutableTheory.indexedOf(u, *scoped(cs))
            },
            FactoryCase("theory/empty-default", false, true, empty = true, defaultUnificator = true) { _, _ ->
                Theory.empty()
            },
            FactoryCase("theory/of-vararg-default", false, true, defaultUnificator = true) { _, cs ->
                Theory.of(*cs.toTypedArray())
            },
            FactoryCase("theory/of-iterable-default", false, true, defaultUnificator = true) { _, cs -> Theory.of(cs) },
            FactoryCase("theory/of-sequence-default", false, true, defaultUnificator = true) { _, cs ->
                Theory.of(cs.asSequence())
            },
            FactoryCase("theory/of-scopes-default", false, true, defaultUnificator = true) { _, cs ->
                Theory.of(*scoped(cs))
            },
        )

private class TagCase(
    val id: String,
    val build: (Clause) -> ClauseCollection,
)

private val tagCases =
    listOf(
        TagCase("list") { ClauseList.of(Unificator.default, it) },
        TagCase("mutable-list") { MutableClauseList.of(Unificator.default, it) },
        TagCase("set") { ClauseSet.of(Unificator.default, it) },
        TagCase("mutable-multiset") { MutableClauseMultiSet.of(Unificator.default, it) },
        TagCase("map-keys") { ClauseMap.of(Unificator.default, it to 1).keys },
        TagCase("theory") { Theory.of(Unificator.default, it) },
        TagCase("mutable-theory") { MutableTheory.of(Unificator.default, it) },
    )

class ClauseCollectionsSpec :
    FunSpec({
        withTests(nameFn = { "factories/${it.id}" }, factories) { case ->
            val unificator = Unificator.naive()
            val given = listOf(p(3), p(1), p("X", "Y"), p(2))
            val expected = if (case.empty) emptyList() else given
            val collection = case.build(unificator, given)
            collection.unificator shouldBe if (case.defaultUnificator) Unificator.default else unificator
            (collection is MutableClauseCollection) shouldBe case.mutable
            if (case.ordered) {
                collection.toList() shouldBe expected
            } else {
                collection.toList() shouldContainExactlyInAnyOrder expected
            }
            collection.size shouldBe expected.size.toLong()
        }

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
                deque.addLast(p("c")).let { oracle.add(p("c")) }
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

        test("list/positional-out-of-bounds") {
            val immutable = ClauseList.of(Unificator.default, clauses(3))
            shouldThrow<IndexOutOfBoundsException> { immutable[3] }
            shouldThrow<IndexOutOfBoundsException> { immutable[-1] }
            val mutable = MutableClauseList.of(Unificator.default, clauses(3))
            shouldThrow<IndexOutOfBoundsException> { mutable[-1] }
            shouldThrow<IndexOutOfBoundsException> { mutable.add(4, p("a")) }
            shouldThrow<IndexOutOfBoundsException> { mutable.set(3, p("a")) }
            shouldThrow<IndexOutOfBoundsException> { mutable.removeAt(3) }
            mutable.toList() shouldBe clauses(3)
        }

        test("deque/ends") {
            val deque = ClauseDeque.of(Unificator.default, clauses(3))
            deque.first() shouldBe p(0)
            deque.firstOrNull() shouldBe p(0)
            deque.last() shouldBe p(2)
            deque.lastOrNull() shouldBe p(2)
            val empty = ClauseDeque.empty()
            empty.firstOrNull().shouldBeNull()
            empty.lastOrNull().shouldBeNull()
            shouldThrow<NoSuchElementException> { empty.first() }
            shouldThrow<NoSuchElementException> { empty.last() }
        }

        test("rules-and-directives") {
            val fact = p(1)
            val rule = Rule.of(Struct.of("q", Var.of("X")), Struct.of("p", Var.of("X")))
            val directive = Directive.of(Atom.of("init"))
            val multiSet = ClauseMultiSet.of(Unificator.default, fact, directive, rule)
            multiSet.rules.toList() shouldContainExactlyInAnyOrder listOf(fact, rule)
            multiSet.directives.toList() shouldBe listOf(directive)
            multiSet.isNonEmpty shouldBe true
            ClauseMultiSet.empty().isNonEmpty shouldBe false
        }

        test("to-string-lists-the-clauses") {
            ClauseList.of(Unificator.default, p(1), p(2)).toString() shouldContain "${p(1)}, ${p(2)}"
            MutableClauseSet.of(Unificator.default, p(1)).toString() shouldContain "p(1)"
            ClauseMap.of(Unificator.default, p(1) to "a").toString() shouldBe "ClauseMap{${p(1)}=a}"
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
            ClauseMultiSet.of(Unificator.default, clauses).hashCode() shouldBe
                MutableClauseMultiSet.of(Unificator.default, clauses.reversed()).hashCode()
        }

        test("set/equality-ignores-order") {
            ClauseSet.of(Unificator.default, clauses(4)) shouldBe
                MutableClauseSet.of(Unificator.default, clauses(4).reversed())
            ClauseSet.of(Unificator.default, clauses(4)).hashCode() shouldBe
                MutableClauseSet.of(Unificator.default, clauses(4).reversed()).hashCode()
            ClauseSet.of(Unificator.default, clauses(4)) shouldNotBe ClauseSet.of(Unificator.default, clauses(3))
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
            set.add(p(Real.of(1.0))) shouldBe true // not a variant of p(1), though they unify
            set[p(1)].toList() shouldBe listOf(p(1), p(Real.of(1.0)))
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
            map.entries.map { it.second }.toList() shouldBe listOf("d")
            map.keys.size shouldBe 1L
            map.clear()
            map.isEmpty shouldBe true
        }

        test("map/factories-and-equality") {
            val first = p("X", 1)
            val entries = listOf(first to "a", p(2) to "b", p("Y", 1) to "c")
            val expected = listOf(first to "c", p(2) to "b")
            val maps =
                listOf(
                    ClauseMap.of(Unificator.default, *entries.toTypedArray()),
                    ClauseMap.of(Unificator.default, entries),
                    MutableClauseMap.of(Unificator.default, *entries.toTypedArray()),
                    MutableClauseMap.of(Unificator.default, entries),
                )
            maps.forAll {
                it.entries.toList() shouldBe expected
                it shouldBe maps.first()
                it.hashCode() shouldBe maps.first().hashCode()
            }
            ClauseMap.empty<Int>().isEmpty shouldBe true
            val naive = Unificator.naive()
            MutableClauseMap.empty<Int>(naive).unificator shouldBeSameInstanceAs naive
            maps.first() shouldNotBe ClauseMap.of(Unificator.default, p(2) to "b")
        }

        withTests(nameFn = { "tags-are-preserved/${it.id}" }, tagCases) { case ->
            val tagged = p("a").setTags(mapOf("k" to "v"))
            val collection = case.build(tagged)
            collection.single() shouldBeSameInstanceAs tagged
            collection[p("a")].single() shouldBeSameInstanceAs tagged
        }

        test("conversions/to-immutable-of-immutable-is-identity") {
            val list = ClauseList.of(Unificator.default, clauses(3))
            list.toImmutable() shouldBeSameInstanceAs list
        }

        test("conversions/to-mutable-copies") {
            val theory = MutableTheory.of(Unificator.default, clauses(3))
            val copy = theory.toMutable()
            copy shouldNotBeSameInstanceAs theory
            copy.add(p("a"))
            theory.toList() shouldBe clauses(3)
            val list = ClauseList.of(Unificator.default, clauses(3))
            list.toMutable().apply { add(p("a")) }.size shouldBe 4L
            list.size shouldBe 3L
        }
    })
