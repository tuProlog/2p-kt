import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unifier.Companion.unifiesWith

fun main() {
    val t = "person"("name"("giovanni"), "surname"("S"), "age"(2))
    val t1 = "person"("name"("N"), "surname"("ciatto"), "age"("2"))
    val t2 = "person"("name"("N"), "surname"("ciatto"("S")), "age"("2"))

    println(t)
    println(t1)
    println(t2)

    println(t unifiesWith t1)
    println(t unifiesWith t2)
}