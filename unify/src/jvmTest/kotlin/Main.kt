import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unifier.Companion.unifiesWith

fun main() {
    val t = "person"("name"("giovanni"), "surname"("S"), "age"(2))
    val t1 = "person"("name"("N"), "surname"("ciatto"), "age"("2"))
    val t2 = "person"("name"("N"), "surname"("ciatto"("S")), "age"("2"))

    println(t)  // person(name(giovanni), surname(S), age(2))
    println(t1) // person(name(N), surname(ciatto), age('2'))
    println(t2) // person(name(N), surname(ciatto(S)), age('2'))


    println(t unifiesWith t1) // {N=giovanni, S=ciatto}
    println(t unifiesWith t2) // OccurCheckException: Cannot match term `person(name(giovanni), surname(S), age(2))`
                              // with term `person(name(N), surname(ciatto(S)), age('2'))` because variable `S` occurs
                              // in term `ciatto(S)`
}