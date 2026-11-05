package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
    })
