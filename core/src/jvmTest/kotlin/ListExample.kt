import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Var

fun main() {


    val l3 = List.from(sequenceOf(Atom.of("1"), Atom.of("2")), Var.of("Tail"))
    println(l3)

    val l2 = List.from(sequenceOf(Atom.of("1")), Var.of("Tail"))
    println(l2)

    try {
        List.from(emptySequence(), Var.of("Tail"))
    } catch (e: IllegalArgumentException) {
        println(e.message)
    }


    val ll3 = List.from(sequenceOf(Atom.of("1"), Atom.of("2"), Var.of("Tail")))
    println(ll3)

    val ll2 = List.from(sequenceOf(Atom.of("1"), Var.of("Tail")))
    println(ll2)

    val ll1 = List.from(sequenceOf(Var.of("Tail")))
    println(ll1)

}