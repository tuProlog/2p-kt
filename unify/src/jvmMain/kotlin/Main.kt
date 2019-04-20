import it.unibo.tuprolog.core.invoke
import it.unibo.tuprolog.unify.Unifier

fun main() {
    val t1 = "person"("name"("N"), "surname"("ciatto"), "age"("1.9"))
    val t2 = "person"("name"("giovanni"), "surname"("S"), "age"(2))

    println(t1)
    println(t2)

    println(Unifier.default.unify(t1, t2))
}