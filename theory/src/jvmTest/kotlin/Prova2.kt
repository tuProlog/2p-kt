import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.ReteTree
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryImpl

val t: List<Clause> = listOf(
        clauseOf("next"("z")),
        ruleOf("next"("0"), 1.toTerm()),
        ruleOf("next"("0"), 2.toTerm()),
        Rule.of("next"("1", "a") as Struct, "a"(1), "b"(2)),
        ruleOf("next"("1", "b"), "a"(1), "b"(3)),
        ruleOf("next"("1", "X"), "a"(1), "b"(3)),
        ruleOf("next"("1", "Y"), "a"(1), "b"(3)),
        directiveOf("a".toTerm(), "b".toTerm(), "c".toTerm()),
        clauseOf("next"("s", ("X")), "next"("X"))
)

fun main() {
    val tt: Theory = Theory.of(t)
    println(ReteTree.of(t).toString(true))
    println(tt.assertA(Struct.of("next", Atom.of("0"))))
}
