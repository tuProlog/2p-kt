package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import it.unibo.tuprolog.core.exception.SubstitutionApplicationException
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.utils.setTag

private class PatternFactoryCase(
    val id: String,
    val build: (Var, kotlin.collections.List<Term>) -> Compound,
)

private class OrderCase(
    val id: String,
    val smaller: () -> Term,
    val greater: () -> Term,
)

class PatternSpec :
    FunSpec({
        val a = Atom.of("a")
        val b = Atom.of("b")
        val sharedFunctor = Var.of("P")

        withTests(
            nameFn = { "factory/${it.id}" },
            PatternFactoryCase("pattern-of-vararg") { f, args -> Pattern.of(f, *args.toTypedArray()) },
            PatternFactoryCase("pattern-of-list") { f, args -> Pattern.of(f, args) },
            PatternFactoryCase("pattern-of-iterable") { f, args -> Pattern.of(f, args.asIterable()) },
            PatternFactoryCase("pattern-of-sequence") { f, args -> Pattern.of(f, args.asSequence()) },
            PatternFactoryCase("compound-of-vararg") { f, args -> Compound.of(f, *args.toTypedArray()) },
            PatternFactoryCase("compound-of-list") { f, args -> Compound.of(f, args) },
            PatternFactoryCase("compound-of-iterable") { f, args -> Compound.of(f, args.asIterable()) },
            PatternFactoryCase("compound-of-sequence") { f, args -> Compound.of(f, args.asSequence()) },
            PatternFactoryCase("scope-pattern-of-vararg") { f, args ->
                Scope.empty { patternOf(f, *args.toTypedArray()) }
            },
            PatternFactoryCase("scope-pattern-of-list") { f, args -> Scope.empty { patternOf(f, args) } },
            PatternFactoryCase("scope-compound-of") { f, args -> Scope.empty { compoundOf(f, args) } },
        ) { case ->
            val p = Var.of("P")
            val x = Var.of("X")
            val pattern = case.build(p, listOf(x, a))
            pattern.shouldBeInstanceOf<Pattern>()
            pattern.functor shouldBeSameInstanceAs p
            pattern.functorTerm shouldBeSameInstanceAs p
            pattern.arity shouldBe 2
            pattern[0] shouldBeSameInstanceAs x
            pattern[1] shouldBe a
            shouldThrow<IllegalArgumentException> { case.build(p, emptyList()) }
        }

        test("indicator/variable-named-preserving-functor-identity") {
            val p = Var.of("P")
            val indicator = Pattern.of(p, a, b).indicator
            indicator.nameTerm shouldBeSameInstanceAs p
            indicator.indicatedArity shouldBe 2
            indicator.indicatedName.shouldBeNull()
            indicator.isWellFormed.shouldBeTrue()
            indicator.isConcrete.shouldBeFalse()
        }

        test("indicator/factory-of-var-name") {
            val p = Var.of("P")
            Indicator.of(p, 1).nameTerm shouldBeSameInstanceAs p
            Scope.empty { indicatorOf(varOf("P"), 1).nameTerm shouldBeSameInstanceAs varOf("P") }
        }

        test("type-checks") {
            val pattern: Term = Pattern.of(Var.of("P"), a)
            pattern.isPattern.shouldBeTrue()
            pattern.isCompound.shouldBeTrue()
            pattern.isStruct.shouldBeFalse()
            pattern.isAtom.shouldBeFalse()
            pattern.isVar.shouldBeFalse()
            pattern.isConstant.shouldBeFalse()
            pattern.isGround.shouldBeFalse()
            pattern.asPattern() shouldBeSameInstanceAs pattern
            pattern.castToPattern() shouldBeSameInstanceAs pattern
            pattern.asStruct().shouldBeNull()
            a.isPattern.shouldBeFalse()
            a.asPattern().shouldBeNull()
            shouldThrow<ClassCastException> { a.castToPattern() }
        }

        test("variables/include-functor") {
            val p = Var.of("P")
            val x = Var.of("X")
            Pattern.of(p, x, a).variables.toList() shouldBe listOf(p, x)
        }

        test("visitor/falls-back-to-visit-compound") {
            val visitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "default"

                    override fun visitCompound(term: Compound) = "compound"
                }
            Pattern.of(Var.of("P"), a).accept(visitor) shouldBe "compound"
        }

        test("visitor/visit-pattern") {
            val visitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "default"

                    override fun visitPattern(term: Pattern) = "pattern"
                }
            Pattern.of(Var.of("P"), a).accept(visitor) shouldBe "pattern"
        }

        test("set-args/retains-functor") {
            val p = Var.of("P")
            val updated = Pattern.of(p, a).setArgs(a, b)
            updated.functor shouldBeSameInstanceAs p
            updated.args shouldBe listOf(a, b)
            shouldThrow<IllegalArgumentException> { Pattern.of(p, a).setArgs(emptyList()) }
        }

        test("apply/functor-to-atom-yields-struct") {
            val p = Var.of("P")
            val x = Var.of("X")
            val result = Pattern.of(p, x).apply(Substitution.of(p to Atom.of("p"), x to a))
            result.shouldBeInstanceOf<Struct>()
            assertEqualities(Struct.of("p", a), result)
        }

        test("apply/functor-to-atom-yields-specialised-struct") {
            val p = Var.of("P")
            val result = Pattern.of(p, a, b).apply(Substitution.of(p, Atom.of(",")))
            result.shouldBeInstanceOf<Tuple>()
        }

        test("apply/functor-to-var-yields-pattern") {
            val p = Var.of("P")
            val q = Var.of("Q")
            val x = Var.of("X")
            val result = Pattern.of(p, x).apply(Substitution.of(p to q, x to a))
            result.shouldBeInstanceOf<Pattern>()
            result.functor shouldBeSameInstanceAs q
            result.args shouldBe listOf(a)
        }

        test("apply/args-only") {
            val p = Var.of("P")
            val x = Var.of("X")
            val result = Pattern.of(p, x).apply(Substitution.of(x, a))
            result.shouldBeInstanceOf<Pattern>()
            result.functor shouldBeSameInstanceAs p
        }

        test("apply/unrelated-substitution-returns-same-instance") {
            val pattern = Pattern.of(Var.of("P"), Var.of("X"))
            pattern.apply(Substitution.of(Var.of("Y"), a)) shouldBeSameInstanceAs pattern
        }

        withTests(
            nameFn = { "apply/invalid-functor-binding/${it.first}" },
            "struct" to Struct.of("f", Atom.of("a")),
            "integer" to Integer.ONE,
            "real" to Real.ONE_HALF,
        ) { (_, value) ->
            val p = Var.of("P")
            shouldThrow<SubstitutionApplicationException> { Pattern.of(p, a).apply(Substitution.of(p, value)) }
        }

        test("apply/nested-pattern-in-struct") {
            val p = Var.of("P")
            val result = Struct.of("g", Pattern.of(p, a)).apply(Substitution.of(p, Atom.of("h")))
            assertEqualities(Struct.of("g", Struct.of("h", a)), result)
        }

        test("fresh-copy/renews-functor-and-args-with-same-scope") {
            val p = Var.of("P")
            val x = Var.of("X")
            val original = Struct.of("f", Pattern.of(p, x), Pattern.of(p, x))
            val copy = original.freshCopy()
            val first = copy[0].castToPattern()
            val second = copy[1].castToPattern()
            first.functor shouldNotBe p
            first[0] shouldNotBe x
            first.functor shouldBeSameInstanceAs second.functor
            first[0] shouldBeSameInstanceAs second[0]
            first.freshCopy().shouldBeInstanceOf<Pattern>()
        }

        test("fresh-copy/preserves-tags") {
            val tagged = Pattern.of(Var.of("P"), Var.of("X")).setTag("origin", "test")
            tagged.freshCopy().tags shouldBe mapOf("origin" to "test")
        }

        test("tags/copy-is-distinct-and-original-untouched") {
            val pattern = Pattern.of(Var.of("P"), a)
            val tagged = pattern.setTag("origin", "test")
            tagged.shouldBeInstanceOf<Pattern>()
            tagged shouldNotBeSameInstanceAs pattern
            tagged.tags shouldBe mapOf("origin" to "test")
            pattern.tags shouldBe emptyMap()
        }

        test("equality/same-functor-and-args") {
            val p = Var.of("P")
            assertEqualities(Pattern.of(p, a), Pattern.of(p, a))
            Pattern.of(p, a).hashCode() shouldBe Pattern.of(p, a).hashCode()
        }

        test("equality/different-functor-vars") {
            Pattern.of(Var.of("P"), a) shouldNotBe Pattern.of(Var.of("P"), a)
        }

        test("equality/pattern-never-equals-struct") {
            val p = Var.of("P")
            (Pattern.of(p, a) == Struct.of("p", a)).shouldBeFalse()
            (Struct.of("p", a) == Pattern.of(p, a)).shouldBeFalse()
        }

        test("structural-equality/up-to-variables") {
            Pattern.of(Var.of("P"), Var.of("X")).structurallyEquals(Pattern.of(Var.of("Q"), Var.of("Y"))).shouldBeTrue()
            Pattern.of(Var.of("P"), a).structurallyEquals(Pattern.of(Var.of("Q"), b)).shouldBeFalse()
            Pattern.of(Var.of("P"), a).structurallyEquals(Struct.of("p", a)).shouldBeFalse()
        }

        test("variants/consistent-renaming") {
            val p = Var.of("P")
            val q = Var.of("Q")
            Pattern.of(p, p).isVariantOf(Pattern.of(q, q)).shouldBeTrue()
            Pattern.of(p, p).isVariantOf(Pattern.of(q, Var.of("Y"))).shouldBeFalse()
            Pattern.of(p, a).isVariantOf(Struct.of("p", a)).shouldBeFalse()
            Pattern.of(p, p).variantHashCode() shouldBe Pattern.of(q, q).variantHashCode()
        }

        test("format/default") {
            val p = Var.of("P")
            Pattern.of(p, a, b).toString() shouldBe "$p(a, b)"
            TermFormatter.prettyVariables().format(Pattern.of(Var.of("P"), Var.of("X"))) shouldBe "P(X)"
        }

        withTests(
            nameFn = { "order/${it.id}" },
            OrderCase("atom-before-pattern", { a }, { Pattern.of(Var.of("P"), a) }),
            OrderCase("var-before-pattern", { Var.of("X") }, { Pattern.of(Var.of("P"), a) }),
            OrderCase("lower-arity-struct-before-pattern", { Struct.of("f", a) }, {
                Pattern.of(Var.of("P"), a, a)
            }),
            OrderCase("pattern-before-same-arity-struct", { Pattern.of(Var.of("P"), b) }, { Struct.of("f", a) }),
            OrderCase("lower-arity-pattern-first", { Pattern.of(Var.of("P"), b) }, { Pattern.of(Var.of("P"), a, a) }),
            OrderCase("same-functor-by-args", { Pattern.of(sharedFunctor, a) }, { Pattern.of(sharedFunctor, b) }),
            OrderCase("nested-pattern-before-struct", { Struct.of("g", Pattern.of(Var.of("P"), a)) }, {
                Struct.of("g", Struct.of("f", a))
            }),
        ) { case ->
            val smaller = case.smaller()
            val greater = case.greater()
            smaller.compareTo(greater) shouldBeLessThan 0
            greater.compareTo(smaller) shouldBeGreaterThan 0
        }

        test("order/same-instance-is-zero") {
            val pattern = Pattern.of(Var.of("P"), a)
            pattern.compareTo(pattern) shouldBe 0
        }

        test("order/same-functor-and-args-is-zero") {
            val p = Var.of("P")
            Pattern.of(p, a).compareTo(Pattern.of(p, a)) shouldBe 0
        }
    })
