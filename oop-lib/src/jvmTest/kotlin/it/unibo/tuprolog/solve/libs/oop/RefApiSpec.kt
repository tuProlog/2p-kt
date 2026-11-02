package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

private val converter = TermToObjectConverter.default

private class InvokeWay(
    val id: String,
    val invoke: (Ref, String, kotlin.collections.List<Term>) -> Result,
)

private val invokeWays =
    listOf(
        InvokeWay("vararg") { ref, name, args -> ref.invoke(name, *args.toTypedArray()) },
        InvokeWay("list") { ref, name, args -> ref.invoke(name, args) },
        InvokeWay("iterable") { ref, name, args -> ref.invoke(name, args.asIterable()) },
        InvokeWay("sequence") { ref, name, args -> ref.invoke(name, args.asSequence()) },
        InvokeWay("converter/vararg") { ref, name, args -> ref.invoke(converter, name, *args.toTypedArray()) },
        InvokeWay("converter/iterable") { ref, name, args -> ref.invoke(converter, name, args.asIterable()) },
        InvokeWay("converter/sequence") { ref, name, args -> ref.invoke(converter, name, args.asSequence()) },
    )

private class CreateWay(
    val id: String,
    val create: (TypeRef, kotlin.collections.List<Term>) -> Result,
)

private val createWays =
    listOf(
        CreateWay("list") { type, args -> type.create(args) },
        CreateWay("iterable") { type, args -> type.create(args.asIterable()) },
        CreateWay("sequence") { type, args -> type.create(args.asSequence()) },
        CreateWay("converter/vararg") { type, args -> type.create(converter, *args.toTypedArray()) },
        CreateWay("converter/iterable") { type, args -> type.create(converter, args.asIterable()) },
        CreateWay("converter/sequence") { type, args -> type.create(converter, args.asSequence()) },
    )

/** A type with a mutable property, to assign through an object reference. */
class Box {
    var value: Int = 0
}

class RefApiSpec :
    FunSpec({
        withTests(nameFn = { "invoke/${it.id}" }, invokeWays) { way ->
            val builder = StringBuilder()
            val result = way.invoke(ObjectRef.of(builder), "append", listOf(Atom.of("x")))
            (result.asObjectRef()?.`object`).shouldBeSameInstanceAs(builder)
            builder.toString() shouldBe "x"
        }

        withTests(nameFn = { "create/${it.id}" }, createWays) { way ->
            val result = way.create(TypeRef.of(StringBuilder::class), listOf(Atom.of("x")))
            (result.asObjectRef()?.`object`.toString()) shouldBe "x"
        }

        test("assign/sets-an-object-property") {
            val box = Box()
            ObjectRef.of(box).assign("value", Integer.of(3)).shouldBeTrue()
            box.value shouldBe 3
        }

        test("assign/sets-a-static-property") {
            TypeRef.of(StaticHolder::class).assign("value", Integer.of(7)).shouldBeTrue()
            StaticHolder.value shouldBe 7
        }

        test("result/a-value") {
            val result = ObjectRef.of(arrayListOf(1, 2)).invoke("size")
            result.isValue().shouldBeTrue()
            result.isNone().shouldBeFalse()
            result.asValue().shouldBeSameInstanceAs(result)
            result.asNone().shouldBeNull()
            result.toTerm() shouldBe Integer.of(2)
        }

        test("result/a-void-method-returns-unit") {
            (
                ObjectRef
                    .of(ArrayList<Any>())
                    .invoke("clear")
                    .asObjectRef()
                    ?.`object`
            ).shouldBeSameInstanceAs(Unit)
        }

        test("result/none") {
            val none = Result.None
            none.isNone().shouldBeTrue()
            none.isValue().shouldBeFalse()
            none.asNone() shouldBeSameInstanceAs none
            none.asValue().shouldBeNull()
            none.toTerm().shouldBeNull()
            none.asObjectRef().shouldBeNull()
        }

        withTests(
            nameFn = { "reference-terms-are-constants/${it.first}" },
            listOf("null" to ObjectRef.NULL, "type" to TypeRef.of(String::class)),
        ) { (_, ref) ->
            val visitor =
                object : TermVisitor<String> {
                    override fun defaultValue(term: Term) = "term"

                    override fun visitAtom(term: Atom) = "atom"
                }
            ref.freshCopy() shouldBeSameInstanceAs ref
            ref.freshCopy(Scope.empty()) shouldBeSameInstanceAs ref
            ref.asConstant() shouldBeSameInstanceAs ref
            (ref.`as`<Atom>()) shouldBeSameInstanceAs ref
            ref.accept(visitor) shouldBe "atom"
        }
    })
