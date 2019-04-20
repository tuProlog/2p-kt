import it.unibo.tuprolog.core.*

val t =  "f"("X", 1, arrayOf("Y", "Z"))


fun main() {
    println(t)
    println(t["X" / 3, "Y" / "a"])
}
