import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ReteTree

val t: List<Clause> = listOf(
        clauseOf("next"("z")),
        factOf("next"("0")),
        factOf("next"("0")),
        Rule.of("next"("1", "a") as Struct, "a"(1), "b"(2)),
        ruleOf("next"("1", "b"), "a"(1), "b"(3)),
        ruleOf("next"("1", "X"), "a"(1), "b"(3)),
        ruleOf("next"("1", "Y"), "a"(1), "b"(3)),
        directiveOf("a".toTerm(), "b".toTerm(), "c".toTerm()),
        clauseOf("next"("s", ("X")), "next"("X"))
)

fun main() {
    println(t)
    println(ReteTree.of(t).toString(true))
}
