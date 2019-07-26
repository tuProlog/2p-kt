import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct

fun main() {
    val scope = Scope.empty()

    val x = with(scope) {
        factOf(structOf("f", varOf("A"), structOf("g", varOf("A")), anonymous(), whatever()))
    }

    val y = x.freshCopy()

    println("x := `$x`")
    println("y := `$y`")

    println("\nFirst A compared to second A in x")

    println(x.head[0] === x.head[1].`as`<Struct>()[0])
    println(x.head[0] == x.head[1].`as`<Struct>()[0])
    println(x.head[0] strictlyEquals x.head[1].`as`<Struct>()[0])

    println("\nFirst _ compared to second _ in x")

    println(x.head[2] === x.head[3])
    println(x.head[2] == x.head[3])
    println(x.head[2] strictlyEquals x.head[3])

    println("\nFirst A compared to second A in y")

    println(y.head[0] === y.head[1].`as`<Struct>()[0])
    println(y.head[0] == y.head[1].`as`<Struct>()[0])
    println(y.head[0] strictlyEquals y.head[1].`as`<Struct>()[0])

    println("\nFirst _ compared to second _ in y")

    println(y.head[2] === y.head[3])
    println(y.head[2] == y.head[3])
    println(y.head[2] strictlyEquals y.head[3])

    println("\nFirst A in x compared to first A in y")

    println(x.head[0] === y.head[0])
    println(x.head[0] == y.head[0])
    println(x.head[0] strictlyEquals y.head[0])

    println("\nSecond A in x compared to second A in y")

    println(x.head[1].`as`<Struct>()[0] === y.head[1].`as`<Struct>()[0])
    println(x.head[1].`as`<Struct>()[0] == y.head[1].`as`<Struct>()[0])
    println(x.head[1].`as`<Struct>()[0] strictlyEquals y.head[1].`as`<Struct>()[0])

    println("\nFirst _ in x compared to first _ in y")

    println(x.head[2] === y.head[2])
    println(x.head[2] == y.head[2])
    println(x.head[2] strictlyEquals y.head[2])

    println("\nSecond _ in x compared to second _ in y")

    println(x.head[3] === y.head[3])
    println(x.head[3] == y.head[3])
    println(x.head[3] strictlyEquals y.head[3])
}