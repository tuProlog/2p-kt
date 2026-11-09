package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.utils.setTags

private class VariantCase(
    val id: String,
    val expected: Boolean,
    val terms: () -> Pair<Term, Term>,
)

private val x get() = Var.of("X")

private val cases =
    listOf(
        VariantCase("renamed-variables", true) {
            Struct.of("f", Var.of("X"), Var.of("Y")) to
                Struct.of("f", Var.of("A"), Var.of("B"))
        },
        VariantCase("shared-vs-distinct", false) {
            val x = Var.of("X")
            Struct.of("f", x, x) to Struct.of("f", Var.of("A"), Var.of("B"))
        },
        VariantCase("distinct-vs-shared", false) {
            val a = Var.of("A")
            Struct.of("f", Var.of("X"), Var.of("Y")) to Struct.of("f", a, a)
        },
        VariantCase("variable-vs-constant", false) { Struct.of("f", Var.of("X")) to Struct.of("f", Atom.of("a")) },
        VariantCase("different-functors", false) { Struct.of("f", Atom.of("a")) to Struct.of("g", Atom.of("a")) },
        VariantCase("integer-vs-real", false) { Integer.of(1) to Real.of(1.0) },
        VariantCase("tags-ignored", true) { Atom.of("a").setTags(mapOf("k" to 1)) to Atom.of("a") },
        VariantCase("same-term", true) { x.let { Struct.of("f", it, it) }.let { it to it } },
        VariantCase("long-lists", true) {
            List.of(kotlin.collections.List(100_000) { Var.of("X") }) to
                List.of(kotlin.collections.List(100_000) { Var.of("Y") })
        },
    )

class VariantsSpec :
    FunSpec({
        withTests(nameFn = { "is-variant-of/${it.id}" }, cases) { case ->
            val (a, b) = case.terms()
            a.isVariantOf(b) shouldBe case.expected
            b.isVariantOf(a) shouldBe case.expected
            if (case.expected) a.variantHashCode() shouldBe b.variantHashCode()
        }
    })
