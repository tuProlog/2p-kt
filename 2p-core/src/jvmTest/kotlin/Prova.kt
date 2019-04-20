import it.unibo.tuprolog.core.*

val t = "f"("X".asVar(), numOf(1), listOf(varOf("Y")).toTerm())


fun main() {
    println(t)
    println(t["X" / numOf(3), "Y" / "a".toTerm()])
}
