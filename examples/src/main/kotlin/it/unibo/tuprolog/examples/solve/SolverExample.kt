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

/** The `it.unibo.tuprolog.solve.Signature` (`gt/2`) under which [gt] is registered as a primitive. */
val gtSignature = Signature("gt", 2)

/**
 * A hand-written `it.unibo.tuprolog.solve.primitive.Primitive` implementing a `gt/2` predicate
 * that succeeds iff its first numeric argument is strictly greater than its second. It is the
 * lowest-level way of extending the solver with custom logic: rather than subclassing one of the
 * `it.unibo.tuprolog.solve.primitive.Primitive` convenience base types (as
 * `it.unibo.tuprolog.examples.solve.ThermostatAgentKt` does), it directly implements the
 * `(Solve.Request<ExecutionContext>) -> Sequence<Solve.Response>` function type expected by
 * `it.unibo.tuprolog.solve.primitive.Primitive`'s constructor.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if either argument is not a
 *   `it.unibo.tuprolog.core.Numeric` term.
 */
fun gt(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> {
    val arg1: Term = request.arguments[0]
    val arg2: Term = request.arguments[1]

    if (arg1 !is Numeric) {
        throw TypeError.forGoal(
            request.context,
            request.signature,
            TypeError.Expected.NUMBER,
            arg1,
        )
    }
    if (arg2 !is Numeric) {
        throw TypeError.forGoal(
            request.context,
            request.signature,
            TypeError.Expected.NUMBER,
            arg2,
        )
    }

    return if (arg1.castTo<Numeric>().decimalValue > arg2.castTo<Numeric>().decimalValue) {
        sequenceOf(request.replySuccess())
    } else {
        sequenceOf(request.replyFail())
    }
}

/**
 * Demonstrates several `:solve` and `:dsl-theory` features together in one runnable program: the
 * `it.unibo.tuprolog.dsl.theory.logicProgramming` DSL block for writing terms/theories/queries
 * with ordinary Kotlin syntax, registering a custom
 * `it.unibo.tuprolog.solve.primitive.Primitive` ([gt]) inside a custom
 * `it.unibo.tuprolog.solve.library.Library`, building a solver with
 * `it.unibo.tuprolog.solve.Solver.prolog.solverWithDefaultBuiltins` combining that library with
 * an inline static knowledge base, and exhaustively pattern-matching over the three
 * `it.unibo.tuprolog.solve.Solution` subtypes (`Yes`, `No`, `Halt`) returned while iterating
 * `it.unibo.tuprolog.solve.Solver.solve`.
 *
 * The static theory defines `user/1` facts for `giovanni` and `lorenzo`, plus a rule that would
 * always fail; the query `user(X), write("hello: "), write(X), nl, gt(2, 1)` asks for every user,
 * prints a greeting for each, and checks the custom `gt/2` primitive succeeds for `2 > 1`.
 *
 * Running this example prints, for each solution, either `yes: ...` with the substitution
 * bindings, `no.`, or `halt: ...` with the logic stack trace, preceded by the `hello: <user>`
 * output written by the `write/1` and `nl/0` standard predicates.
 */
fun main() {
    logicProgramming {
        val solver =
            Solver.prolog.solverWithDefaultBuiltins(
                otherLibraries =
                    Runtime.of(
                        Library.of(
                            alias = "it.unibo.lrizzato.myprimives",
                            primitives = mapOf(gtSignature to Primitive(::gt)),
                        ),
                    ),
                staticKb =
                    theoryOf(
                        fact { "user"("giovanni") },
                        fact { "user"("lorenzo") },
                        rule { "user"(`_`) impliedBy fail },
                    ),
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
