package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import org.gciatto.kt.math.BigInteger
import org.junit.Test

class TestRefs {
    @Test
    fun method() {
        val ref = ObjectRef.of(StringBuilder())
        ref.invoke("append", Atom.of("cacca"))
        ref.invoke("append", Integer.of(BigInteger.of(Long.MAX_VALUE) + BigInteger.ONE))
        val y = ref.invoke("toString")
        println(y)
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
