package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.concurrent.ConcurrentSolver
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Negation as failure.
 * Conceptually equivalent to
 * ```
 * \+(G) :- call(G), !, fail.
 * ```
 * in classic Prolog.
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
