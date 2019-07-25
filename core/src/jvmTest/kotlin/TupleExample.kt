import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

fun main() {


    val tuple = Tuple.of(Atom.of("first"), Numeric.of(2), Var.of("Third"))

    println(tuple)
    println(tuple.left)
    println(tuple.right)
    println(tuple.right.`as`<Tuple>().left)
    println(tuple.right.`as`<Tuple>().right)

    println(tuple.unfoldedList)
    println(tuple.unfoldedList == tuple.toList())
    println(tuple.unfoldedArray)
    println(tuple.unfoldedArray.contentEquals(tuple.toArray()))
    println(tuple.unfoldedSequence)
    println(tuple.unfoldedSequence == tuple.toSequence())

    println(Tuple.wrapIfNeeded(Atom.of("first"), Numeric.of(2), Var.of("Third")))
    println(Tuple.wrapIfNeeded(Atom.of("first"), Numeric.of(2)))
    println(Tuple.wrapIfNeeded(Atom.of("first")))
    println(Tuple.wrapIfNeeded())

}