import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.libs.oop.*
import org.gciatto.kt.math.BigInteger
import kotlin.test.Test

class Prova {
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
        val y = x.toTerm().`as`<ObjectRef>().invoke("println", Atom.of("cacca"))
        println(y)
    }

    @Test
    fun creation() {
        val ref = TypeFactory.default.typeRefFromName("java.lang.String")!!
        val x = ref.create()
        println(x.asObjectRef())
    }
}