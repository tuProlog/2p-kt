package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.libs.oop.exceptions.TermToObjectConversionException
import org.gciatto.kt.math.BigInteger
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

class TestRefs {
    @Test
    fun methodWithSmartCast() {
        val ref = ObjectRef.of(StringBuilder())
        ref.invoke("append", Atom.of("a"))
        val bigInt = Integer.of(BigInteger.of(Long.MAX_VALUE) + BigInteger.ONE)
        ref.invoke("append", bigInt)
        val res = ref.invoke("toString")
        val expected = "a${bigInt}"
        assertEquals(expected, res.asObjectRef()?.`object`)
        assertEquals(Atom.of(expected), res.toTerm())
        assertNotEquals(ObjectRef.of(expected), res.asObjectRef())
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
