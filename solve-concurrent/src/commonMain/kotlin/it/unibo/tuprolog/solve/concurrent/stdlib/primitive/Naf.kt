package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Negation as failure (`\+/1`).
 * Conceptually equivalent to
 * ```
 * \+(G) :- call(G), !, fail.
 * ```
 * in classic Prolog, but implemented directly as a [it.unibo.tuprolog.solve.primitive.Primitive] here, spawning an
 * independent [ConcurrentSolver] (via `subSolver()`) to check whether its argument has at least one solution,
 * rather than as a rule. Since [it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins] registers this object
 * under the very same `\+/1` signature as the rule-based
 * [it.unibo.tuprolog.solve.concurrent.stdlib.rule.NegationAsFailure], and primitives are looked up before rules
 * (see `it.unibo.tuprolog.solve.concurrent.fsm.StatePrimitiveSelection`), this primitive always wins and that rule
 * pair is currently dead code (see the `// TODO remove rule for \+ from default builtins` comment next to this
 * object's own registration in `DefaultBuiltins`).
 */
object Naf : UnaryPredicate.NonBacktrackable<ConcurrentExecutionContext>("\\+") {
    override fun Solve.Request<ConcurrentExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsCallable(0)
        val solver = subSolver() as ConcurrentSolver
        val solution = solver.solveOnce(first.castToStruct())
        return when (solution) {
            is Solution.Yes -> replyFail()
            is Solution.Halt -> replyException(solution.exception.pushContext(context))
            else -> replySuccess()
        }
    }
}
