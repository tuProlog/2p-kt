import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Var

fun main() {


    val l3 = List.from(sequenceOf(Atom.of("1"), Atom.of("2")), Var.of("Tail"))
    println(l3)
    println(l3.freshCopy())
    println(l3.freshCopy() == l3)

    val l2 = List.from(sequenceOf(Atom.of("1")), Var.of("Tail"))
    println(l2)
    println(l2.freshCopy())
    println(l2.freshCopy() == l2)

    try {
        List.from(emptySequence(), Var.of("Tail"))
    } catch (e: IllegalArgumentException) {
        println(e.message)
    }


    val ll3 = List.from(sequenceOf(Atom.of("1"), Atom.of("2"), Var.of("Tail")))
    println(ll3)
    println(ll3.freshCopy())
    println(ll3.freshCopy() == ll3)

    val ll2 = List.from(sequenceOf(Atom.of("1"), Var.of("Tail")))
    println(ll2)
    println(ll2.freshCopy())
    println(ll2.freshCopy() == ll2)

    val ll1 = List.from(sequenceOf(Var.of("Tail")))
    println(ll1)
    println(ll2.freshCopy())
    println(ll2.freshCopy() == ll2)

}