package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.List as LogicList
import kotlin.collections.List as KtList

/** A lazy list whose first two items are `a` and `b`, and whose remaining items throw when evaluated. */
private fun throwingSuffixList(): LogicList =
    LogicList.of(
        sequence {
            yield(Atom.of("a"))
            yield(Atom.of("b"))
            error("suffix evaluated")
        },
    )

/** A recursive term wrapping a lazy [LogicList], by means of a constructor that must not traverse it. */
internal data class WrapCase(
    val id: String,
    val wrap: (LogicList) -> Recursive,
)

private val wrapCases =
    listOf(
        WrapCase("cons") { Cons.of(Atom.of("x"), it) },
        WrapCase("tuple") { Tuple.of(Atom.of("x"), it) },
        WrapCase("block") { Block.of(it as Term) },
    )

/** A generator of `[X0, …, Xn-1]`-like items, with [ground] deciding whether they are atoms or variables. */
internal data class MetadataCase(
    val id: String,
    val ground: Boolean,
) {
    fun items(): KtList<Term> = (0 until 5).map { if (ground) Atom.of("a$it") else Var.of("X$it") }
}

private val metadataCases = listOf(MetadataCase("ground", true), MetadataCase("non-ground", false))

/** A recursive term whose flattened snapshots must be array-backed. */
internal data class SnapshotCase(
    val id: String,
    val build: (KtList<Term>) -> Recursive,
)

private val snapshotCases =
    listOf(
        SnapshotCase("list-eager") { LogicList.of(it) },
        SnapshotCase("list-lazy") { LogicList.of(it.asSequence()) },
        SnapshotCase("tuple") { Tuple.of(it) },
        SnapshotCase("block") { Block.of(it) },
    )

/** A sequence of [size] atoms, which counts how many of them have been [produced] so far. */
internal class CountedSource(
    size: Int,
) {
    var produced = 0
        private set

    val items: KtList<Term> = (0 until size).map { Atom.of("a$it") }

    val sequence: Sequence<Term> =
        sequence {
            for (item in items) {
                produced++
                yield(item)
            }
        }
}

private fun throwingSequence(): Sequence<Term> = sequence { error("suffix evaluated") }

/** A way of building a recursive term out of a sequence, which must only consume as much of it as needed. */
internal data class LazyConstructionCase(
    val id: String,
    val buildFromSequence: (Sequence<Term>) -> Recursive,
    val buildFromList: (KtList<Term>) -> Recursive,
) {
    fun build(items: Sequence<Term>): Recursive = buildFromSequence(items)

    fun build(items: KtList<Term>): Recursive = buildFromList(items)
}

private val lazyConstructionCases =
    listOf(
        LazyConstructionCase("tuple", { Tuple.of(it) }, { Tuple.of(it) }),
        LazyConstructionCase("tuple-from-iterable", { Tuple.of(it.asIterable()) }, { Tuple.of(it) }),
        LazyConstructionCase("scope-tuple-of", { Scope.empty().tupleOf(it) }, { Tuple.of(it) }),
        LazyConstructionCase("block", { Block.of(it) }, { Block.of(it) }),
        LazyConstructionCase("block-from-iterable", { Block.of(it.asIterable()) }, { Block.of(it) }),
        LazyConstructionCase("scope-block-of", { Scope.empty().blockOf(it) }, { Block.of(it) }),
    )

class RecursiveLazinessSpec :
    FunSpec({
        withTests(nameFn = { "wrap/${it.id}-does-not-traverse-lazy-child" }, wrapCases) { case ->
            val wrapped = case.wrap(throwingSuffixList())
            wrapped.args.size shouldBe wrapped.arity
        }

        withTests(nameFn = { "meta/cons-over-lazy-tail/${it.id}" }, metadataCases) { case ->
            val items = case.items()
            val lazy = Cons.of(items[0], LogicList.of(items.drop(1).asSequence()))
            val eager = LogicList.of(items).castToCons()
            lazy.size shouldBe eager.size
            lazy.isGround shouldBe eager.isGround
            lazy.isGround shouldBe case.ground
            lazy.last shouldBe eager.last
            lazy.isWellFormed shouldBe eager.isWellFormed
            lazy shouldBe eager
        }

        withTests(nameFn = { "meta/tuple-over-lazy-list/${it.id}" }, metadataCases) { case ->
            val items = case.items()
            val tuple = Tuple.of(Atom.of("x"), LogicList.of(items.asSequence()))
            tuple.isGround shouldBe case.ground
            tuple.size shouldBe 2
        }

        withTests(nameFn = { "meta/block-over-lazy-list/${it.id}" }, metadataCases) { case ->
            val block = Block.of(LogicList.of(case.items().asSequence()) as Term)
            block.isGround shouldBe case.ground
            block.size shouldBe 1
        }

        withTests(nameFn = { "materialization/${it.id}-is-random-access" }, snapshotCases) { case ->
            val term = case.build((0 until 5).map { Var.of("X$it") })
            (term.unfoldedList is RandomAccess) shouldBe true
            (term.toList() is RandomAccess) shouldBe true
        }

        test("apply/lazy-list-with-explicit-last-stays-lazy") {
            val x = Var.of("X")
            val list =
                LogicList.from(
                    sequence {
                        yield(x)
                        yield(Atom.of("b"))
                        error("suffix evaluated")
                    },
                    Var.of("T"),
                )
            val applied = list.apply(Substitution.of(x, Atom.of("a"))).castToCons()
            applied.head shouldBe Atom.of("a")
            applied.tail.castToCons().head shouldBe Atom.of("b")
        }

        test("apply/lazy-list-with-explicit-last-substitutes-last") {
            val t = Var.of("T")
            val list = LogicList.from(sequenceOf(Atom.of("a"), Atom.of("b")), t)
            val applied = list.apply(Substitution.of(t, Atom.of("c"))).castToList()
            applied.last shouldBe Atom.of("c")
            applied shouldBe LogicList.from(listOf(Atom.of("a"), Atom.of("b")), Atom.of("c"))
        }

        test("apply/lazy-list-with-implicit-last-stays-lazy") {
            val x = Var.of("X")
            val list =
                LogicList.from(
                    sequence {
                        yield(x)
                        yield(Atom.of("b"))
                        yield(Atom.of("c"))
                        error("suffix evaluated")
                    },
                )
            val applied = list.apply(Substitution.of(x, Atom.of("a"))).castToCons()
            applied.head shouldBe Atom.of("a")
            applied.tail.castToCons().head shouldBe Atom.of("b")
        }

        test("fresh-copy/lazy-list-with-implicit-last-stays-lazy") {
            val x = Var.of("X")
            val list =
                LogicList.from(
                    sequence {
                        yield(x)
                        yield(Atom.of("b"))
                        yield(Atom.of("c"))
                        error("suffix evaluated")
                    },
                )
            val copy = list.freshCopy().castToCons()
            copy.head.isVar shouldBe true
            (copy.head == x) shouldBe false
            copy.tail.castToCons().head shouldBe Atom.of("b")
        }

        test("fresh-copy/lazy-list-with-explicit-last-stays-lazy") {
            val x = Var.of("X")
            val list =
                LogicList.from(
                    sequence {
                        yield(x)
                        yield(Atom.of("b"))
                        error("suffix evaluated")
                    },
                    Var.of("T"),
                )
            val copy = list.freshCopy().castToCons()
            copy.head.isVar shouldBe true
            (copy.head == x) shouldBe false
            copy.tail.castToCons().head shouldBe Atom.of("b")
        }

        test("meta/lazy-list-with-explicit-last-ending-in-a-list") {
            val (a, b, c, d) = listOf("a", "b", "c", "d").map { Atom.of(it) }
            val list = LogicList.from(sequenceOf(a, b), LogicList.of(c, d))
            list.last shouldBe LogicList.empty()
            list.isWellFormed shouldBe true
            list.size shouldBe 4
            list shouldBe LogicList.of(a, b, c, d)
        }

        withTests(nameFn = { "construction/${it.id}/consumes-bounded-prefix" }, lazyConstructionCases) { case ->
            val source = CountedSource(1_000)
            val term = case.build(source.sequence)
            source.produced shouldBeLessThanOrEqual 3
            term.unfoldedSequence.take(10).toList() shouldBe source.items.take(10)
            source.produced shouldBeLessThanOrEqual 12
        }

        withTests(nameFn = { "construction/${it.id}/prefix-on-throwing-suffix" }, lazyConstructionCases) { case ->
            val (a, b, c) = listOf(Atom.of("a"), Atom.of("b"), Atom.of("c"))
            val term =
                case.build(
                    sequence {
                        yield(a)
                        yield(b)
                        yield(c)
                        error("suffix evaluated")
                    },
                )
            term.unfoldedSequence.take(2).toList() shouldBe listOf(a, b)
        }

        withTests(
            nameFn = { "construction/${it.id}/lazy-is-indistinguishable-from-eager" },
            lazyConstructionCases,
        ) { case ->
            val items = (0 until 150).map { if (it % 3 == 0) Var.of("X$it") else Atom.of("a$it") }
            val lazy = case.build(items.asSequence())
            val eager = case.build(items)
            (lazy == eager) shouldBe true
            (eager == lazy) shouldBe true
            lazy.hashCode() shouldBe eager.hashCode()
            lazy.toString() shouldBe eager.toString()
            lazy.structurallyEquals(eager) shouldBe true
            lazy.compareTo(eager) shouldBe 0
            lazy.size shouldBe eager.size
            lazy.isGround shouldBe eager.isGround
            lazy.unfoldedList shouldBe eager.unfoldedList
            lazy.args shouldBe eager.args
        }

        withTests(nameFn = { "construction/${it.id}/substitution-stays-lazy" }, lazyConstructionCases) { case ->
            val x = Var.of("X")
            val term =
                case.build(
                    sequence {
                        yield(x)
                        yield(Atom.of("b"))
                        yield(Atom.of("c"))
                        error("suffix evaluated")
                    },
                )
            val applied = term.apply(Substitution.of(x, Atom.of("a"))).castToRecursive()
            applied.unfoldedSequence.take(2).toList() shouldBe listOf(Atom.of("a"), Atom.of("b"))
        }

        test("construction/tuple/requires-two-items") {
            shouldThrow<IllegalArgumentException> { Tuple.of(emptySequence<Term>()) }
            shouldThrow<IllegalArgumentException> { Tuple.of(sequenceOf<Term>(Atom.of("a"))) }
        }

        test("construction/block/zero-and-one-items") {
            Block.of(emptySequence<Term>()) shouldBe Block.empty()
            Block.of(sequenceOf<Term>(Atom.of("a"))) shouldBe Block.of(Atom.of("a"))
        }

        test("construction/tuple/wrap-if-needed-is-lazy") {
            val wrapped = Tuple.wrapIfNeeded(sequenceOf<Term>(Atom.of("a"), Atom.of("b")) + throwingSequence())
            wrapped.castToTuple().left shouldBe Atom.of("a")
        }
    })
