package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.libs.oop.exceptions.OopRuntimeException
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import org.junit.Test
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class TestRefs {

    private data class TestDatum(val term: Term, val type: KClass<*>, val converted: Any?) {
        val string: String get() = type.name

        constructor(obj: Any?, type: KClass<*>) :
            this(obj?.let { ObjectRef.of(obj) } ?: ObjectRef.NULL, type, obj)
    }

    private val cases = listOf(
        TestDatum(Atom.of("a"), String::class, "a"),
        TestDatum(Integer.ONE, BigInteger::class, BigInteger.ONE),
        TestDatum(Real.ONE, BigDecimal::class, BigDecimal.ONE),
        TestDatum(Truth.TRUE, Boolean::class, true),
        TestDatum(Truth.FAIL, Boolean::class, false),
        TestDatum(Truth.FALSE, Boolean::class, false),
        TestDatum(Var.anonymous(), Any::class, null),
        TestDatum(Var.of("X"), Any::class, null),
        TestDatum(A.B(1, "a"), Any::class),
        TestDatum(A.C(2, "b"), Any::class),
        TestDatum(null, Any::class),
    )

    @Test
    fun mostProperOverloadIsSelected() {
        val obj = OverloadDetector.create()
        val ref = ObjectRef.of(obj)
        for (case in cases) {
            try {
                val result = ref.invoke("call", case.term)
                assertTrue { result is Result.Value }
                assertEquals(case.string, result.asObjectRef()?.`object`)
                assertEquals(Atom.of(case.string), result.toTerm())
            } catch (e: OopRuntimeException) {
                assertTrue { case.term.let { it is Var || it is NullRef } }
            }
        }
        var result = ref.invoke("size")
        val expectedSize = cases.size - cases.filter { it.term is Var || it.term is NullRef }.count()
        assertEquals(Integer.of(expectedSize), result.toTerm())
        result = ref.invoke("toList")
        val list = result.asObjectRef()?.`object` as List<*>
        assertEquals(cases.map { it.converted }.filterNotNull(), list)
        assertEquals(cases.map { it.converted to it.type }.filter { (c, _) -> c != null }, obj.recordings)
    }

    @Test
    fun methodWithExplicitCast() {
        val ref = ObjectRef.of(StringBuilder())
        ref.invoke("append", Atom.of("a"))
        val bigInt = Integer.of(BigInteger.of(Long.MAX_VALUE) + BigInteger.ONE)
        ref.invoke("append", Struct.of("as", bigInt, Atom.of(BigInteger::class.fullName)))
        val res = ref.invoke("toString")
        val expected = "a$bigInt"
        assertEquals(expected, res.asObjectRef()?.`object`)
        assertEquals(Atom.of(expected), res.toTerm())
        assertNotEquals(ObjectRef.of(expected), res.asObjectRef())
    }

    @Test
    fun methodWithFailedExplicitCast() {
        val ref = ObjectRef.of(StringBuilder())
        ref.invoke("append", Atom.of("a"))
        val bigInt = Integer.of(BigInteger.of(Long.MAX_VALUE) + BigInteger.ONE)
        try {
            ref.invoke("append", Struct.of("as", bigInt, Atom.of(Long::class.fullName)))
            fail()
        } catch (e: TermToObjectConversionException) {
            assertEquals(bigInt, e.term)
            assertEquals(Long::class, e.targetType)
        }
    }

    @Test
    fun staticMethod() {
        val ref = TypeRef.of(System::class)
        val x = ref.invoke("out") as Result.Value
        val y = x.toTerm().castTo<ObjectRef>().invoke("println", Atom.of("cacca"))
        println(y)
    }

    @Test
    fun creation() {
        val ref = TypeFactory.default.typeRefFromName("java.lang.String")!!
        val x = ref.create()
        println(x.asObjectRef())
    }
}
