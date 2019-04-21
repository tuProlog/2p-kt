import it.unibo.tuprolog.core.*

val t =  clauseOf("fact"(1))


fun main() {
    println(t)
    println(t["X" / 3, "Y" / "a"])
}
