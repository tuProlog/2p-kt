package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.solver.statemachine.TimeDuration

/** A base class for Solve requests and responses */
sealed class Solve {

    /** The context that was actual at Request making or that changed after solving the goal */
    abstract val context: ExecutionContext?


    /** Class representing a Request to be full-filled by the Solver */
    data class Request(
            /** Signature of the goal to be solved */
            val signature: Signature,
            /** Arguments with which the goal is invoked */
            val arguments: List<Term>,
            /** The context that's actual at Request making */
            override val context: ExecutionContext,
            /** The executionTimeout after which the computation can end, because no more useful */
            val executionTimeout: TimeDuration = Long.MAX_VALUE
    ) : Solve() {
        init {
            require(executionTimeout >= 0) { "The execution timeout can't be negative: $executionTimeout" }
        }
    }


    /** Class representing a Response, from the Solver, to a [Solve.Request] */
    data class Response(
            /** The solution attached to the response */
            val solution: Solution,
            /** The changed execution context after goal solving, or `null` if nothing changed in context */
            override val context: ExecutionContext? = null
    ) : Solve()
}
