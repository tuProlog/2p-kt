package it.unibo.tuprolog.solve.libs.oop

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
            assertSame<Any?>(builder, result.asObjectRef()?.`object`)
            assertEquals("x", builder.toString())
        }

        withTests(nameFn = { "create/${it.id}" }, createWays) { way ->
            val result = way.create(TypeRef.of(StringBuilder::class), listOf(Atom.of("x")))
            assertEquals("x", result.asObjectRef()?.`object`.toString())
        }

        test("assign/sets-an-object-property") {
            val box = Box()
            assertTrue(ObjectRef.of(box).assign("value", Integer.of(3)))
            assertEquals(3, box.value)
        }

        test("assign/sets-a-static-property") {
            assertTrue(TypeRef.of(StaticHolder::class).assign("value", Integer.of(7)))
            assertEquals(7, StaticHolder.value)
        }

        test("result/a-value") {
            val result = ObjectRef.of(arrayListOf(1, 2)).invoke("size")
            assertTrue(result.isValue())
            assertFalse(result.isNone())
            assertSame<Any?>(result, result.asValue())
            assertNull(result.asNone())
            assertEquals(Integer.of(2), result.toTerm())
        }

        test("result/a-void-method-returns-unit") {
            assertSame<Any?>(
                Unit,
                ObjectRef
                    .of(ArrayList<Any>())
                    .invoke("clear")
                    .asObjectRef()
                    ?.`object`,
            )
        }

        test("result/none") {
            val none = Result.None
            assertTrue(none.isNone())
            assertFalse(none.isValue())
            assertSame(none, none.asNone())
            assertNull(none.asValue())
            assertNull(none.toTerm())
            assertNull(none.asObjectRef())
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
            assertSame(ref, ref.freshCopy())
            assertSame(ref, ref.freshCopy(Scope.empty()))
            assertSame(ref, ref.asConstant())
            assertSame(ref, ref.`as`<Atom>())
            assertEquals("atom", ref.accept(visitor))
        }
    })
