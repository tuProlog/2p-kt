import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unifier.Companion.mguWith
import it.unibo.tuprolog.unify.Unifier.Companion.unifyWith
import it.unibo.tuprolog.unify.Unifier.Companion.matches


fun main() {
    val t = "person"("name"("giovanni"), "surname"("S"), "age"(2))
    val t1 = "person"("name"("N"), "surname"("ciatto"), "age"("2"))
    val t2 = "person"("name"("N"), "surname"("ciatto"("S")), "age"("2"))

    println(t)  // person(name(giovanni), surname(S), age(2))
    println(t1) // person(name(N), surname(ciatto), age('2'))
    println(t2) // person(name(N), surname(ciatto(S)), age('2'))


    println(t.mguWith(t1)) // {N=giovanni, S=ciatto}
    println(t.unifyWith(t1)) // person(name(giovanni), surname(ciatto), age(2))
    println(t.matches(t1)) // true

    println(t.mguWith(t2)) // null
    println(t.unifyWith(t2)) // null
    println(t.matches(t2)) // false
}