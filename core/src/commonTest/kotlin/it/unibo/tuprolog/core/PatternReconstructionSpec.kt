package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlin.reflect.KClass

/** Instantiating the functor of a pattern to [functor], with [args], must yield an instance of [type]. */
private class Reconstruction(
    val id: String,
    val functor: String,
    val args: () -> kotlin.collections.List<Term>,
    val type: KClass<out Term>,
)

private val a = Atom.of("a")
private val b = Atom.of("b")

private val reconstructions =
    listOf(
        Reconstruction("mr-67/cons", ".", { listOf(a, EmptyList()) }, Cons::class),
        Reconstruction("mr-68/tuple", ",", { listOf(a, b) }, Tuple::class),
        Reconstruction("mr-69/block", "{}", { listOf(a) }, Block::class),
        Reconstruction("mr-70/rule", ":-", { listOf(Struct.of("h", a), b) }, Rule::class),
        Reconstruction("mr-70/fact", ":-", { listOf(Struct.of("h", a), Truth.TRUE) }, Fact::class),
        Reconstruction("mr-70/goal", ":-", { listOf(a) }, Goal::class),
        Reconstruction("mr-71/indicator", "/", { listOf(a, Integer.of(1)) }, Indicator::class),
        Reconstruction("mr-72/ordinary-struct", "f", { listOf(a) }, Struct::class),
    )

/** Patterns rebuild, once their functors are instantiated, the terms the canonical factories build (MR-67 to MR-76). */
class PatternReconstructionSpec :
    FunSpec({
        withTests(nameFn = { it.id }, reconstructions) { case ->
            val functor = Var.of("P")
            val args = case.args()
            val instantiated = Pattern.of(functor, args).apply(Substitution.of(functor, Atom.of(case.functor)))
            case.type.isInstance(instantiated) shouldBe true
            instantiated shouldBe Struct.of(case.functor, args)
            instantiated::class shouldBe Struct.of(case.functor, args)::class
        }

        test("mr-73/pattern-functor-term-is-its-variable") {
            val functor = Var.of("P")
            Pattern.of(functor, a).functorTerm shouldBeSameInstanceAs functor
        }

        test("mr-74/struct-functor-term-is-an-atom") {
            Struct.of("f", a).functorTerm shouldBe Atom.of("f")
        }

        test("mr-75/pattern-indicator-is-variable-named") {
            val functor = Var.of("P")
            val indicator = Pattern.of(functor, a, b).indicator
            indicator.nameTerm shouldBeSameInstanceAs functor
            indicator.arityTerm shouldBe Integer.of(2)
            indicator.isWellFormed shouldBe true
            indicator.isConcrete.shouldBeFalse()
        }

        test("mr-76/instantiated-pattern-indicator-is-concrete") {
            val functor = Var.of("P")
            val pattern = Pattern.of(functor, a, b)
            val substitution = Substitution.of(functor, Atom.of("f"))
            val indicator = pattern.indicator.apply(substitution)
            indicator.shouldBeInstanceOf<Indicator>()
            indicator shouldBe Indicator.of("f", 2)
            indicator.isConcrete shouldBe true
            indicator shouldBe pattern.apply(substitution).castToStruct().indicator
        }
    })
