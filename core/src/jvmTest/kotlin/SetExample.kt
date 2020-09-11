import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Set
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

fun main() {
    val s = Set.of(Atom.of("first"), Numeric.of(2), Var.of("Third"))

    println(s)
    println(s[0])
    println(s[0] is Tuple)

    try {
        println(s[1])
    } catch (e: Exception) {
        println(e.message)
    }

    println(s.unfoldedList)
    println(s.unfoldedList == s.toList())
    println(s.unfoldedArray)
    println(s.unfoldedArray.contentEquals(s.toArray()))
    println(s.unfoldedSequence)
    println(s.unfoldedSequence == s.toSequence())
}
