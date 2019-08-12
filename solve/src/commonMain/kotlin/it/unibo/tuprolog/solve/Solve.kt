package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature

/** A base class for Solve requests and responses */
sealed class Solve {

    /** Signature of the goal to be solved or that has been solved */
    abstract val signature: Signature
    /** Arguments with which the goal has been invoked */
    abstract val arguments: List<Term>
    /** The context that was actual at Request making or that changed after solving the goal */
    abstract val context: ExecutionContext?


    /** Class representing a Request to be full-filled by the Solver */
    data class Request(
            /** Signature of the goal to be solved */
            override val signature: Signature,
            /** Arguments with which the goal is invoked */
            override val arguments: List<Term>,
            /** The context that's actual at Request making */
            override val context: ExecutionContext
    ) : Solve()


    /** Class representing a Response, from the Solver, to a [Solve.Request] */
    data class Response(
            /** Signature of the goal that has been solved */
            override val signature: Signature,
            /** Arguments with which the goal was invoked */
            override val arguments: List<Term>,
            /** The solution attached */
            val solution: Solution,
            /** The changed execution context after goal solving, or `null` if nothing changed in context */
            override val context: ExecutionContext? = null
    ) : Solve()
}
