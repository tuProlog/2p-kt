package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.List as LogicList
import kotlin.collections.List as KtList

private const val DEPTH = 100_000

/** A way of building a [DEPTH]-long recursive term out of its items. */
internal data class DeepKind(
    val id: String,
    val build: (KtList<Term>) -> Recursive,
)

/** An operation that must not overflow the native stack on [DEPTH]-long, non-ground recursive terms. */
internal data class DeepOperation(
    val id: String,
    val check: (items: KtList<Term>, build: (KtList<Term>) -> Recursive) -> Unit,
)

internal data class DeepCase(
    val kind: DeepKind,
    val operation: DeepOperation,
) {
    val id: String get() = "${kind.id}/${operation.id}"
}

private val kinds =
    listOf(
        DeepKind("list-eager") { LogicList.of(it) },
        DeepKind("list-lazy") { LogicList.of(it.asSequence()) },
        DeepKind("list-lazy-implicit-last") { LogicList.from(it.asSequence()) },
        DeepKind("tuple") { Tuple.of(it) },
        DeepKind("block") { Block.of(it) },
    )

private val operations =
    listOf(
        DeepOperation("fresh-copy") { items, build ->
            val copy = build(items).freshCopy()
            copy.isGround shouldBe false
            copy.unfoldedSequence.first().isVar shouldBe true
            (copy.unfoldedSequence.first() == items.first()) shouldBe false
        },
        DeepOperation("apply") { items, build ->
            val substitution = Substitution.of(items.first().castToVar(), Atom.of("a"))
            val result = build(items).apply(substitution)
            result.castToRecursive().unfoldedSequence.first() shouldBe Atom.of("a")
            result.isGround shouldBe false
        },
        DeepOperation("equals") { items, build ->
            (build(items) == build(items)) shouldBe true
        },
        DeepOperation("structurally-equals") { items, build ->
            build(items).structurallyEquals(build(items)) shouldBe true
        },
        DeepOperation("compare") { items, build ->
            build(items).compareTo(build(items)) shouldBe 0
        },
        DeepOperation("hash-code") { items, build ->
            build(items).hashCode() shouldBe build(items).hashCode()
        },
        DeepOperation("to-string") { items, build ->
            build(items).toString().length shouldBeGreaterThan DEPTH
        },
    )

private val deepCases = kinds.flatMap { kind -> operations.map { DeepCase(kind, it) } }

private fun deepItems(): KtList<Term> = (0 until DEPTH).map { Var.of("X$it") }

/** A non-ground list/tuple of [size] variables, where only the first one is going to be substituted. */
internal data class SharingCase(
    val id: String,
    val build: (KtList<Term>) -> Struct,
    val suffix: (Struct) -> Term,
    val size: Int = 50,
)

private val sharingCases =
    listOf(
        SharingCase("tuple-apply-keeps-untouched-suffix", { Tuple.of(it) }, { it.castToTuple().right }),
        SharingCase("cons-apply-keeps-untouched-suffix", { LogicList.of(it) }, { it.castToCons().tail }),
        SharingCase("long-cons-apply-keeps-untouched-suffix", { LogicList.of(it) }, { it.castToCons().tail }, 1_000),
        SharingCase(
            "struct-apply-keeps-untouched-args",
            { Struct.of("f", it[0], Struct.of("g", it.drop(1))) },
            { it[1] },
        ),
    )

class DeepRecursiveTermsSpec :
    FunSpec({
        withTests(nameFn = { "deep/${it.id}" }, deepCases) { case ->
            case.operation.check(deepItems(), case.kind.build)
        }

        withTests(nameFn = { "sharing/${it.id}" }, sharingCases) { case ->
            val items = (0 until case.size).map { Var.of("X$it") }
            val original = case.build(items)
            val result = original.apply(Substitution.of(items.first(), Atom.of("a"))).castToStruct()
            (case.suffix(result) === case.suffix(original)) shouldBe true
        }

        test("sharing/struct-apply-unchanged-returns-same-instance") {
            val term = Struct.of("f", Var.of("X"), Struct.of("g", Var.of("Y")))
            (term.apply(Substitution.of(Var.of("Z"), Atom.of("a"))) === term) shouldBe true
        }

        test("sharing/equals-stops-at-shared-lazy-suffix") {
            val shared =
                LogicList.of(
                    sequence {
                        yield(Atom.of("a"))
                        yield(Atom.of("b"))
                        error("suffix evaluated")
                    },
                )
            (Cons.of(Atom.of("x"), shared) == Cons.of(Atom.of("x"), shared)) shouldBe true
            Cons.of(Atom.of("x"), shared).structurallyEquals(Cons.of(Atom.of("x"), shared)) shouldBe true
            Cons.of(Atom.of("x"), shared).compareTo(Cons.of(Atom.of("x"), shared)) shouldBe 0
        }
    })
