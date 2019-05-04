import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ReteTree

val t: List<Clause> = listOf(
        clauseOf("next"("z")),
        factOf("next"("0")),
        clauseOf("next"("s", ("X")), "next"("X"))
)

fun main() {
    println(t)
    println(ReteTree.of(t).toString(true))
}
