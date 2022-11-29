package it.unibo.tuprolog.examples.solve

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.Solve

val gtSignature = Signature("gt", 2)

fun gt(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
    val arg1: Term = request.arguments[0]
    val arg2: Term = request.arguments[1]

    if (arg1 !is Numeric) {
        throw TypeError.forGoal(
            request.context,
            request.signature,
            TypeError.Expected.NUMBER,
            arg1
        )
    }
    if (arg2 !is Numeric) {
        throw TypeError.forGoal(
            request.context,
            request.signature,
            TypeError.Expected.NUMBER,
            arg2
        )
    }

    return if (arg1.castTo<Numeric>().decimalValue > arg2.castTo<Numeric>().decimalValue) {
        sequenceOf(request.replySuccess())
    } else {
        sequenceOf(request.replyFail())
    }
}

fun main() {
    logicProgramming {
        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Runtime.of(
                Library.of(
                    alias = "it.unibo.lrizzato.myprimives",
                    primitives = mapOf(gtSignature to Primitive(::gt))
                )
            ),
            staticKb = theoryOf(
                fact { "user"("giovanni") },
                fact { "user"("lorenzo") },
                rule { "user"(`_`) impliedBy fail }
            )
        )
        val query = "user"("X") and "write"("hello: ") and "write"("X") and "nl" and "gt"(2, 1)
        solver.solve(query).forEach {
            when (it) {
                is Solution.No -> println("no.\n")
                is Solution.Yes -> {
                    println("yes: ${it.solvedQuery}")
                    for (assignment in it.substitution) {
                        println("\t${assignment.key} / ${assignment.value}")
                    }
                    println()
                }
                is Solution.Halt -> {
                    println("halt: ${it.exception.message}")
                    for (err in it.exception.logicStackTrace) {
                        println("\t $err")
                    }
                }
            }
        }
    }
}
