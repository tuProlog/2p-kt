package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.StructUtils

private class CompoundTermCase(
    val id: String,
    val term: Term,
    val isCompound: Boolean,
)

private class InvalidFunctorCase(
    val id: String,
    val functor: Any,
)

class CompoundSpec :
    FunSpec({
        withTests(nameFn = { "of/dispatches-to-struct-of/vararg/${it.id}" }, StructUtils.mixedStructCases) { case ->
            val expected = Struct.of(case.functor, case.args)
            val actual = Compound.of(case.functor, *case.args.toTypedArray())
            assertEqualities(expected, actual)
            actual::class shouldBe expected::class
        }

        withTests(nameFn = { "of/dispatches-to-struct-of/list/${it.id}" }, StructUtils.mixedStructCases) { case ->
            val expected = Struct.of(case.functor, case.args)
            val actual = Compound.of(case.functor, case.args)
            assertEqualities(expected, actual)
            actual::class shouldBe expected::class
        }

        withTests(nameFn = { "of/dispatches-to-struct-of/iterable/${it.id}" }, StructUtils.mixedStructCases) { case ->
            val expected = Struct.of(case.functor, case.args)
            val actual = Compound.of(case.functor, case.args.asIterable())
            assertEqualities(expected, actual)
            actual::class shouldBe expected::class
        }

        withTests(nameFn = { "of/dispatches-to-struct-of/sequence/${it.id}" }, StructUtils.mixedStructCases) { case ->
            val expected = Struct.of(case.functor, case.args)
            val actual = Compound.of(case.functor, case.args.asSequence())
            assertEqualities(expected, actual)
            actual::class shouldBe expected::class
        }

        withTests(nameFn = { "of/atom-functor/${it.id}" }, StructUtils.mixedStructCases) { case ->
            val expected = Struct.of(case.functor, case.args)
            val actual = Compound.of(Atom.of(case.functor), case.args)
            assertEqualities(expected, actual)
            actual::class shouldBe expected::class
        }

        withTests(nameFn = { "scope/compound-of/${it.id}" }, StructUtils.mixedStructCases) { case ->
            val expected = Struct.of(case.functor, case.args)
            val actual = Scope.empty { compoundOf(case.functor, case.args) }
            assertEqualities(expected, actual)
            actual::class shouldBe expected::class
        }

        test("scope/compound-of/preserves-variable-identity") {
            Scope.empty {
                val compound = compoundOf("f", varOf("X"), varOf("X"))
                compound[0] shouldBeSameInstanceAs varOf("X")
                compound[1] shouldBeSameInstanceAs varOf("X")
            }
        }

        test("of/reserved/empty-list") {
            Compound.of("[]").shouldBeInstanceOf<EmptyList>()
        }

        test("of/reserved/empty-block") {
            Compound.of("{}").shouldBeInstanceOf<EmptyBlock>()
        }

        test("of/reserved/tuple") {
            Compound.of(",", Atom.of("a"), Atom.of("b")).shouldBeInstanceOf<Tuple>()
        }

        test("of/reserved/rule") {
            Compound.of(":-", Atom.of("a"), Atom.of("b")).shouldBeInstanceOf<Rule>()
        }

        test("of/reserved/true") {
            Compound.of("true").shouldBeInstanceOf<Truth>()
        }

        withTests(
            nameFn = { "of/rejects-invalid-functor/${it.id}" },
            InvalidFunctorCase("integer-term", Integer.ONE),
            InvalidFunctorCase("real-term", Real.ONE_HALF),
            InvalidFunctorCase("struct-term", Struct.of("f", Atom.of("a"))),
            InvalidFunctorCase("host-int", 42),
            InvalidFunctorCase("host-object", Any()),
        ) { case ->
            shouldThrow<IllegalArgumentException> { Compound.of(case.functor, Atom.of("a")) }
        }

        test("functor/covariant-string-for-struct") {
            val compound: Compound = Struct.of("f", Atom.of("a"), Var.of("X"))
            compound.functor shouldBe "f"
            compound.functorTerm shouldBe Atom.of("f")
            compound.indicator shouldBe Indicator.of("f", 2)
            compound.arity shouldBe 2
        }

        test("functor/struct-indicator-consistent-with-functor-term") {
            val struct = Struct.of("g", Atom.of("a"))
            struct.indicator shouldBe Indicator.of(struct.functorTerm, Integer.of(struct.arity))
        }

        test("set-args/returns-struct-of-same-functor") {
            val compound: Compound = Struct.of("f", Atom.of("a"))
            val updated = compound.setArgs(listOf(Atom.of("b"), Atom.of("c")))
            assertEqualities(Struct.of("f", Atom.of("b"), Atom.of("c")), updated)
        }

        withTests(
            nameFn = { "is-compound/${it.id}" },
            CompoundTermCase("struct", Struct.of("f", Atom.of("a")), true),
            CompoundTermCase("atom", Atom.of("a"), true),
            CompoundTermCase("empty-list", EmptyList(), true),
            CompoundTermCase("list", List.of(Integer.ONE), true),
            CompoundTermCase("tuple", Tuple.of(Atom.of("a"), Atom.of("b")), true),
            CompoundTermCase("rule", Rule.of(Atom.of("a"), Atom.of("b")), true),
            CompoundTermCase("indicator", Indicator.of("a", 1), true),
            CompoundTermCase("var", Var.of("X"), false),
            CompoundTermCase("integer", Integer.ONE, false),
            CompoundTermCase("real", Real.ONE_HALF, false),
        ) { case ->
            case.term.isCompound shouldBe case.isCompound
            if (case.isCompound) {
                case.term.asCompound() shouldBeSameInstanceAs case.term
                case.term.castToCompound() shouldBeSameInstanceAs case.term
            } else {
                case.term.asCompound().shouldBeNull()
                shouldThrow<ClassCastException> { case.term.castToCompound() }
            }
        }

        withTests(
            nameFn = { "visitor/struct-falls-back-to-visit-compound/${it.id}" },
            CompoundTermCase("struct", Struct.of("f", Atom.of("a")), true),
            CompoundTermCase("atom", Atom.of("a"), true),
            CompoundTermCase("list", List.of(Integer.ONE), true),
            CompoundTermCase("rule", Rule.of(Atom.of("a"), Atom.of("b")), true),
            CompoundTermCase("var", Var.of("X"), false),
            CompoundTermCase("integer", Integer.ONE, false),
        ) { case ->
            val visitor =
                object : TermVisitor<Boolean> {
                    override fun defaultValue(term: Term) = false

                    override fun visitCompound(term: Compound) = true
                }
            case.term.accept(visitor) shouldBe case.isCompound
        }
    })
