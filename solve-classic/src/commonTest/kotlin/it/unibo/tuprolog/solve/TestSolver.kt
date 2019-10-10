package it.unibo.tuprolog.solve

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.DefaultBuiltins
import it.unibo.tuprolog.theory.ClauseDatabase
import org.gciatto.kt.math.BigInteger

val dummyLibraries = Libraries(
        DefaultBuiltins
)

val dummyStatic = ClauseDatabase.of(
        { ruleOf(structOf("a", varOf("X")), structOf("b", varOf("X"))) },
        { factOf(structOf("a", numOf(6))) },
        { ruleOf(structOf("b", varOf("X")), structOf("c", varOf("X")), structOf("d", varOf("X"))) },
        { ruleOf(structOf("b", numOf(4)), atomOf("!")) },
        { factOf(structOf("b", numOf(5))) },
        { factOf(structOf("c", numOf(1))) },
        { ruleOf(structOf("c", numOf(2)), atomOf("!")) },
        { factOf(structOf("c", numOf(3))) },
        { factOf(structOf("d", numOf(2))) },
        { factOf(structOf("d", numOf(3))) },

        { ruleOf(structOf("x", varOf("X")), structOf("y", varOf("X")), structOf("z", varOf("X")), atomOf("!"), structOf("w", varOf("X"))) },
        { factOf(structOf("y", numOf(1))) },
        { factOf(structOf("y", numOf(2))) },
        { factOf(structOf("y", numOf(3))) },
        { factOf(structOf("z", numOf(2))) },
        { factOf(structOf("z", numOf(3))) },
        { factOf(structOf("w", whatever())) }
)

fun main(args: Array<String>) {
    val solver: MutableSolver = MutableSolver(libraries = dummyLibraries, staticKB = dummyStatic)

    println(dummyLibraries.theory)
    println(dummyStatic)

    println()

    solver.solve { structOf("member", varOf("N"), listOf(numOf(3), numOf(2), numOf(1))) }
        .forEach { println(it); println(it.solvedQuery) }

    println("---")

    solver.solve { structOf("a", varOf("X")) }
            .forEach { println(it); println(it.solvedQuery) }

    println("---")

    solver.solve { structOf("x", varOf("X")) }
            .forEach { println(it); println(it.solvedQuery) }

    println("---")

    solver.solve { structOf("call", structOf(";", structOf(">", numOf(1), numOf(2)), structOf(">", numOf(1), atomOf("X")))) }
            .forEach {
                println(it);
                (it as Solution.Halt).exception.prologStackTrace.forEach(::println)
            }

    println("---")

    solver.solve { tupleOf(structOf("natural", varOf("X")), structOf(">", varOf("X"), numOf(5)), structOf("=<", varOf("X"), numOf(20))) }
            .take(15). forEach{ println(it); println(it.solvedQuery) }

    println("---")

    solver.solve { structOf("natural", numOf(BigInteger.of(Long.MAX_VALUE) + BigInteger.ONE)) }
            .forEach{ println(it); println(it.solvedQuery) }

    println("---")

    solver.solve {
        structOf("catch",
                structOf("call", structOf(";", structOf(">", numOf(1), numOf(2)), tupleOf(structOf(">", numOf(1), numOf(-3.5)), structOf("throw", structOf("error", structOf("type_error", atomOf("number"), varOf("A")), atomOf("info")))))),
                varOf("Error"),
                structOf("=", varOf("E"), varOf("Error"))
        )
    }.forEach { println(it); }

}