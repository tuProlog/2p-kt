package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.NullRef
import it.unibo.tuprolog.core.ObjectRef
import it.unibo.tuprolog.core.Ref
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Runtime
import kotlin.reflect.KClass

private fun solve(goal: Struct): Solution =
    Solver.prolog.solverWithDefaultBuiltins(otherLibraries = Runtime.of(OOPLib)).solveOnce(goal)

/** A host object whose method returns a logic term. */
class TermSource {
    fun term(): Term = Struct.of("f", Atom.of("a"))

    fun typeName(type: KClass<*>): String = type.simpleName!!
}

private class KindCase(
    val id: String,
    val ref: Term,
    val ref1: Boolean,
    val objectRef: Boolean,
    val nullRef: Boolean,
    val typeRef: Boolean,
)

private val kindCases =
    listOf(
        KindCase("object", Ref.of(StringBuilder()), ref1 = true, objectRef = true, nullRef = false, typeRef = false),
        KindCase("null", Ref.of(null), ref1 = true, objectRef = false, nullRef = true, typeRef = false),
        KindCase("type", TypeRef.of(String::class), ref1 = true, objectRef = false, nullRef = false, typeRef = true),
        KindCase("atom", Atom.of("a"), ref1 = false, objectRef = false, nullRef = false, typeRef = false),
    )

/** The OOP library works with the references of the core: object, null and type references. */
class CoreRefsSpec :
    FunSpec({
        test("type-refs-are-object-refs-of-their-types") {
            val ref = TypeRef.of(String::class)
            ref.shouldBeInstanceOf<ObjectRef>()
            ref.type shouldBe String::class
            ref.value shouldBe String::class
        }

        test("type-refs-of-a-type-are-equal") {
            TypeRef.of(String::class) shouldBe TypeRef.of(String::class)
            TypeRef.of(String::class).hashCode() shouldBe TypeRef.of(String::class).hashCode()
            TypeRef.of(String::class) shouldNotBe TypeRef.of(Int::class)
            // equal classes may be distinct objects
            TypeRef.of(Boolean::class) shouldBe TypeRef.of(Class.forName("java.lang.Boolean").kotlin)
        }

        test("object-refs-are-equal-by-identity") {
            val list = arrayListOf(1)
            Ref.of(list) shouldBe Ref.of(list)
            Ref.of(list) shouldNotBe Ref.of(arrayListOf(1))
        }

        test("results-of-null-are-null-refs") {
            ObjectToTermConverter.default.convert(null) shouldBeSameInstanceAs NullRef.instance
            Result.Value(null).asRef() shouldBe NullRef.instance
        }

        test("host-terms-convert-to-themselves") {
            val result = Ref.of(TermSource()).invoke("term")
            result.toTerm() shouldBe Struct.of("f", Atom.of("a"))
        }

        test("type-refs-convert-to-their-types-as-arguments") {
            val result = Ref.of(TermSource()).invoke("typeName", TypeRef.of(String::class))
            result.toTerm() shouldBe Atom.of("String")
        }

        withTests(nameFn = { "kinds/${it.id}" }, kindCases) { case ->
            fun holds(predicate: String) = solve(Struct.of(predicate, case.ref)).isYes
            holds("ref") shouldBe case.ref1
            holds("object_ref") shouldBe case.objectRef
            holds("null_ref") shouldBe case.nullRef
            holds("type_ref") shouldBe case.typeRef
        }

        test("predicates-take-type-refs") {
            val list = Var.of("L")
            solve(Struct.of("new_object", TypeRef.of(ArrayList::class), List.empty(), list)).isYes shouldBe true
            val cast = solve(Struct.of("cast", Atom.of("x"), TypeRef.of(String::class), Var.of("R")))
            cast.isYes shouldBe true
        }

        test("object-refs-unify-by-identity") {
            val list = arrayListOf(1)
            solve(Struct.of("=", Ref.of(list), Ref.of(list))).isYes shouldBe true
            solve(Struct.of("=", Ref.of(list), Ref.of(arrayListOf(1)))).isYes shouldBe false
        }
    })
